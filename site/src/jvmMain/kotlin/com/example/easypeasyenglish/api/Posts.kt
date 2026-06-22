package com.example.easypeasyenglish.api



import com.example.easypeasyenglish.data.MongoRepository
import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.ApiResponse
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Params.CAT_PARAM
import com.example.easypeasyenglish.models.Params.POST_ID_PARAM
import com.example.easypeasyenglish.models.Params.QUERY_PARAM
import com.example.easypeasyenglish.models.Params.SKIP_PARAM
import com.example.easypeasyenglish.models.Post
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.ObjectIdGenerator

@Api("addpost")
suspend fun addPost(context: ApiContext) {
    try {
        val post = context.req.getBody<Post>()
        val newPost = post?.copy(postId = ObjectIdGenerator.newObjectId<String>().id.toHexString())
        context.res.setBody(
            newPost?.let {
                context.logger.info("Inserting post:$post")
                context.data.getValue<MongoRepository>().addPost(it).toString()
            } ?: false.toString()
        )
    } catch (e: Exception) {
        context.logger.info("Api Error: $e")
        context.res.setBody(e.message)
    }
}

@Api("readmyposts")
suspend fun readMyPosts(context: ApiContext) {
   try {
       val skip = context.req.params["skip"]?.toInt() ?: 0
       val author = context.req.params["author"] ?: ""
       val myPosts = context.data.getValue<MongoRepository>().readMyPosts(skip = skip, author = author)
       context.res.setBody(ApiListResponse.Success(data = myPosts))
   } catch (e: Exception) {
       context.res.setBody(ApiListResponse.Error(e.message.toString()))
   }
}

@Api("readMainPosts")
suspend fun readMainPosts(context: ApiContext) {
    try {
        val mainPosts = context.data.getValue<MongoRepository>().readMainPosts()
        context.res.setBody(data = ApiListResponse.Success(mainPosts))
    } catch (e: Exception) {
        context.res.setBody(ApiResponse.Error(e.toString()))
    }
}

@Api("readLatestPosts")
suspend fun readLatestPosts(context: ApiContext) {
    try {
        val latestPostsToSkip = context.req.params["skip"]?.toInt() ?: 0
        val latestPosts = context.data.getValue<MongoRepository>().readLatestPosts(latestPostsToSkip)
        context.res.setBody(data = ApiListResponse.Success(latestPosts))
    } catch (e: Exception) {
        context.res.setBody(ApiResponse.Error(e.toString()))
    }
}

@Api("readSponsoredPosts")
suspend fun readSponsoredPosts(context: ApiContext) {
    try {

        val sponsoredPosts = context.data.getValue<MongoRepository>().readSponsoredPosts()
        context.res.setBody(data = ApiListResponse.Success(sponsoredPosts))
    } catch (e: Exception) {
        context.res.setBody(ApiResponse.Error(e.toString()))
    }
}

@Api("readPopularPosts")
suspend fun readPopularPosts(context: ApiContext) {
    try {
        val popularPostsToSkip = context.req.params["skip"]?.toInt() ?: 0
        val popularPosts = context.data.getValue<MongoRepository>().readLatestPosts(popularPostsToSkip)
        context.res.setBody(data = ApiListResponse.Success(popularPosts))
    } catch (e: Exception) {
        context.res.setBody(ApiResponse.Error(e.toString()))
    }
}
@Api("deleteSelectedPosts")
suspend fun deleteSelectedPosts(context: ApiContext) {
   try {
       val request = context.req.getBody<List<String>>()
       context.res.setBody(
           request?.let {
           context.data.getValue<MongoRepository>().deleteSelectedPosts(request).toString()
       } ?: "false"
       )
   } catch (e: Exception) {
       context.res.setBody(e.message)
   }
}

@Api("searchPostsByTitle")
suspend fun searchPostsByTitle(context: ApiContext) {
    try {
        val query = context.req.params[QUERY_PARAM] ?: ""
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val request = context.data.getValue<MongoRepository>().searchPostsByTitle(query = query, skip = skip)

        context.res.setBody(ApiListResponse.Success(data = request))
    } catch (e: Exception) {
        println(e)
        context.res.setBody(ApiListResponse.Error(e.message.toString()))
    }
}

@Api("searchPostsByCategory")
suspend fun searchPostsByCategory(context: ApiContext) {
    try {
        val category = Category.valueOf(context.req.params[CAT_PARAM] ?: Category.Design.name)
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val request = context.data.getValue<MongoRepository>().searchPostsByCategory(category = category, skip = skip)

        context.res.setBody(ApiListResponse.Success(data = request))
    } catch (e: Exception) {
        context.res.setBody(ApiListResponse.Error(e.message.toString()))
    }
}

@Api("updatePost")
suspend fun updatePost(context: ApiContext) {
    try {
        val updatingPost = context.req.getBody<Post>()
        val response = updatingPost?.let {
            context.data.getValue<MongoRepository>().updatePost(post = updatingPost)
        }
        context.res.setBody(response ?: false)
    } catch (e: Exception) {
        context.res.setBody(e.message)
    }

}
@Api("readSelectedPost")
suspend fun readSelectedPost(context: ApiContext) {
    val postId = context.req.params[POST_ID_PARAM]
    if (!postId.isNullOrEmpty()) {
        return try {
            val selectedPost = context.data.getValue<MongoRepository>().readSelectedPost(id = postId)
            context.res.setBody(ApiResponse.Success(data = selectedPost))
        } catch (e: Exception) {
            context.res.setBody(ApiResponse.Error(e.message.toString()))
        }
    } else {
       context.res.setBody(ApiResponse.Error("Selected post doesn't exist!"))
    }
}

inline fun <reified T> Response.setBody(data: T) {
    setBodyText(Json.encodeToString(data))
}

inline fun <reified T> Request.getBody(): T? {
    return body?.decodeToString()?.let { Json.decodeFromString(it) }
}