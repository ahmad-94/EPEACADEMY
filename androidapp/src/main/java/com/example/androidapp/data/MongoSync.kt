package com.example.androidapp.data

import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.RealmConfiguration
import io.github.xilinjia.krdb.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object MongoSync {
    private val config = RealmConfiguration.Builder(schema = setOf(LocalPost::class))
        .compactOnLaunch()
        .build()

    val realm: Realm = Realm.open(config)

    suspend fun insertPost(post: LocalPost) {
        realm.write {
            copyToRealm(post, io.github.xilinjia.krdb.UpdatePolicy.ALL)
        }
    }

    suspend fun insertPosts(posts: List<LocalPost>) {
        realm.write {
            posts.forEach { copyToRealm(it, io.github.xilinjia.krdb.UpdatePolicy.ALL) }
        }
    }

    fun readAllPosts(): Flow<List<LocalPost>> {
        return realm.query<LocalPost>().asFlow().map { it.list }
    }

    fun readAllPostsSync(): List<LocalPost> {
        return realm.query<LocalPost>().find()
    }

    fun readMainPosts(): Flow<List<LocalPost>> {
        return realm.query<LocalPost>("main == true").asFlow().map { it.list }
    }

    fun readMainPostsSync(): List<LocalPost> {
        return realm.query<LocalPost>("main == true").find()
    }

    fun readPopularPosts(): Flow<List<LocalPost>> {
        return realm.query<LocalPost>("popular == true").asFlow().map { it.list }
    }

    fun readPopularPostsSync(): List<LocalPost> {
        return realm.query<LocalPost>("popular == true").find()
    }

    fun readSponsoredPosts(): Flow<List<LocalPost>> {
        return realm.query<LocalPost>("sponsored == true").asFlow().map { it.list }
    }

    fun readSponsoredPostsSync(): List<LocalPost> {
        return realm.query<LocalPost>("sponsored == true").find()
    }

    fun readPostById(postId: String): Flow<LocalPost?> {
        return realm.query<LocalPost>("postId == $0", postId).asFlow().map { it.list.firstOrNull() }
    }

    fun readPostByIdSync(postId: String): LocalPost? {
        return realm.query<LocalPost>("postId == $0", postId).find().firstOrNull()
    }

    suspend fun deletePost(postId: String) {
        realm.write {
            val post = query<LocalPost>("postId == $0", postId).find().firstOrNull()
            if (post != null) {
                delete(post)
            }
        }
    }

    suspend fun deleteMainPosts() {
        realm.write {
            val posts = query<LocalPost>("main == true").find()
            delete(posts)
        }
    }

    suspend fun deletePopularPosts() {
        realm.write {
            val posts = query<LocalPost>("popular == true").find()
            delete(posts)
        }
    }

    suspend fun deleteSponsoredPosts() {
        realm.write {
            val posts = query<LocalPost>("sponsored == true").find()
            delete(posts)
        }
    }

    suspend fun deleteAllPosts() {
        realm.write {
            val posts = query<LocalPost>().find()
            delete(posts)
        }
    }
}
