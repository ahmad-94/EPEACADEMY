package com.example.easypeasyenglish.data

import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Newsletter
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.models.User
import com.example.easypeasyenglish.utils.Constants.DATABASE_NAME
import com.example.easypeasyenglish.utils.Constants.MAIN_POSTS_LIMIT
import com.example.easypeasyenglish.utils.Constants.POSTS_PER_PAGE
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.regex
import org.litote.kmongo.setValue


@InitApi
fun initMongoDB(context: InitApiContext) {
    System.setProperty(
        "org.litote.mongo.test.mapping.service",
        "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
    )

    val mongoDB = MongoDB(context)
    context.data.add<MongoRepository>(mongoDB)
}




class MongoDB(
    private val context: InitApiContext

): MongoRepository {

    private val client by lazy { KMongo.createClient(resolveMongoUri()) }
//private val client = KMongo.createClient()

    private val database by lazy { client.getDatabase(DATABASE_NAME) }
    private val userCollection by lazy { database.getCollection<User>() }
    private val postCollection by lazy { database.getCollection<Post>() }
    private val newsletterCollection by lazy { database.getCollection<Newsletter>() }

    private fun resolveMongoUri(): String {
        System.getenv("MONGODB_URI")?.let { return it }

        val isProd = System.getProperty("kobweb.server.environment") == "PROD"
        if (isProd) {
            error("MONGODB_URI environment variable is not set")
        }

        context.logger.warn("MONGODB_URI not set; using mongodb://localhost:27017 for local development")
        return "mongodb://localhost:27017"
    }


    override suspend fun addPost(post: Post): Boolean {
        return postCollection.insertOne(post).awaitFirst().wasAcknowledged()
    }

    override suspend fun updatePost(post: Post): Boolean {
        return postCollection
            .updateOne(
                Post::postId eq post.postId,
                mutableListOf(
                    setValue(Post::title, post.title),
                    setValue(Post::subtitle, post.subtitle),
                    setValue(Post::category, post.category),
                    setValue(Post::thumbnail, post.thumbnail),
                    setValue(Post::content, post.content),
                    setValue(Post::main, post.main),
                    setValue(Post::popular, post.popular),
                    setValue(Post::sponsored, post.sponsored)
                )
            )
            .awaitSingle()
            .wasAcknowledged()
    }

    override suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(PostWithoutDetails::author eq author )
            .sort(descending(PostWithoutDetails::date))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
    }

    override suspend fun readMainPosts(): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(PostWithoutDetails::main eq true)
            .sort(descending(PostWithoutDetails::date))
            .limit(MAIN_POSTS_LIMIT)
            .toList()
    }

    override suspend fun readLatestPosts(skip: Int): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(
                and(
                    PostWithoutDetails::main eq false,
                    PostWithoutDetails::sponsored eq false,
                    PostWithoutDetails::popular eq false,
                )
            )
            .sort(descending(PostWithoutDetails::date))
            .limit(POSTS_PER_PAGE)
            .skip(skip)
            .toList()
    }

    override suspend fun readAllPosts(skip: Int): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find()
            .sort(descending(PostWithoutDetails::date))
            .limit(POSTS_PER_PAGE)
            .skip(skip)
            .toList()
    }

    override suspend fun readSponsoredPosts(): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(PostWithoutDetails::sponsored eq true)
            .limit(2)
            .sort(descending(PostWithoutDetails::date))
            .toList()
    }

    override suspend fun readPopularPosts(skip: Int): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(PostWithoutDetails::popular eq true)
            .sort(descending(PostWithoutDetails::date))
            .limit(POSTS_PER_PAGE)
            .skip(skip)
            .toList()
    }

    override suspend fun searchPostsByTitle(
        query: String,
        skip: Int,
    ): List<PostWithoutDetails> {
        val regexQuery = query.toRegex(RegexOption.IGNORE_CASE)
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(PostWithoutDetails::title regex regexQuery)
            .sort(descending(PostWithoutDetails::date))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
    }

    override suspend fun searchPostsByCategory(
        category: Category,
        skip: Int,
    ): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(PostWithoutDetails::category eq category)
            .sort(descending(PostWithoutDetails::date))
            .skip(skip)
            .toList()
    }

    override suspend fun readSelectedPost(id: String): Post {
        return postCollection.find(Post::postId eq id).toList().first()
    }

    override suspend fun deleteSelectedPosts(selectedPosts: List<String>): Boolean {
        return postCollection
            .deleteMany(Post::postId `in` selectedPosts)
            .awaitLast()
            .wasAcknowledged()
    }

    override suspend fun checkUserExistence(user: User): User? {
        context.logger.info(
            "Checking user in MongoDB: username=${user.username}, password=${user.password}"
        )
        return try { userCollection
                .find(
                    and(
                        User::username eq user.username,
                        User::password eq user.password
                    )
                ).awaitFirstOrNull()
        } catch (e: Exception) {
            context.logger.error("MongoDB query failed; ${e.message.toString()}")
            null
        }
    }

    override suspend fun checkUserId(id: String): Boolean {
        return try {
            val documentCount = userCollection.countDocuments(User::id eq id).awaitFirst()
            documentCount > 0
        } catch (e: Exception) {
            context.logger.error("MongoDB query failed; ${e.message.toString()}")
            false
        }
    }

    override suspend fun subscribe(newsletter: Newsletter): String {
        val email = newsletterCollection
            .find(Newsletter::email eq newsletter.email)
            .toList()
        return if (email.isNotEmpty()) {
            "You're already subscribed!"
        } else {
            val newEmail = newsletterCollection
                .insertOne(newsletter)
                .awaitFirst()
                .wasAcknowledged()
            if (newEmail) "Successfully Subscribed!"
            else "Something went wrong, try again later!"
        }
    }

}