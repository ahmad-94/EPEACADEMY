package com.example.androidapp.repository

import com.example.androidapp.data.LocalPost
import com.example.androidapp.data.MongoSync
import com.example.androidapp.data.toLocal
import com.example.androidapp.data.toPost
import com.example.androidapp.data.toPostWithoutDetails
import com.example.androidapp.network.ApiService
import com.example.androidapp.util.RequestState
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout

class PostRepository(
    private val apiService: ApiService
) {

    fun getMainPosts(): Flow<RequestState<List<PostWithoutDetails>>> = flow {
        emit(RequestState.Loading())
        var localPosts: List<PostWithoutDetails> = emptyList()
        try {
            localPosts = MongoSync.readMainPostsSync().map { it.toPostWithoutDetails() }
            if (localPosts.isNotEmpty()) {
                emit(RequestState.Loading(localPosts))
            }
        } catch (t: Throwable) {
        }

        try {
            val posts = withTimeout(120000) {
                apiService.getMainPosts()
            }
            MongoSync.deleteMainPosts()
            MongoSync.insertPosts(posts.map { it.toLocal().apply { main = true } })
            emit(RequestState.Success(posts))
        } catch (t: Throwable) {
            if (localPosts.isNotEmpty()) {
                emit(RequestState.Success(localPosts))
            } else {
                emit(RequestState.Error(t))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getLatestPosts(skip: Int): Flow<RequestState<List<PostWithoutDetails>>> = flow {
        emit(RequestState.Loading())
        var localPosts: List<PostWithoutDetails> = emptyList()
        try {
            if (skip == 0) {
                localPosts = MongoSync.readAllPostsSync().map { it.toPostWithoutDetails() }
                if (localPosts.isNotEmpty()) {
                    emit(RequestState.Loading(localPosts))
                }
            }
        } catch (t: Throwable) {
        }

        try {
            val posts = withTimeout(120000) {
                apiService.getLatestPosts(skip)
            }
            if (skip == 0) {
                MongoSync.deleteAllPosts()
            }
            MongoSync.insertPosts(posts.map { it.toLocal() })
            emit(RequestState.Success(posts))
        } catch (t: Throwable) {
            if (skip == 0 && localPosts.isNotEmpty()) {
                emit(RequestState.Success(localPosts))
            } else {
                emit(RequestState.Error(t))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getAllPosts(skip: Int): Flow<RequestState<List<PostWithoutDetails>>> = flow {
        emit(RequestState.Loading())
        var localPosts: List<PostWithoutDetails> = emptyList()
        try {
            if (skip == 0) {
                localPosts = MongoSync.readAllPostsSync().map { it.toPostWithoutDetails() }
                if (localPosts.isNotEmpty()) {
                    emit(RequestState.Loading(localPosts))
                }
            }
        } catch (t: Throwable) {
        }

        try {
            val posts = withTimeout(120000) {
                apiService.getAllPosts(skip)
            }
            if (skip == 0) {
                MongoSync.deleteAllPosts()
            }
            MongoSync.insertPosts(posts.map { it.toLocal() })
            emit(RequestState.Success(posts))
        } catch (t: Throwable) {
            if (skip == 0 && localPosts.isNotEmpty()) {
                emit(RequestState.Success(localPosts))
            } else {
                emit(RequestState.Error(t))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getSponsoredPosts(): Flow<RequestState<List<PostWithoutDetails>>> = flow {
        emit(RequestState.Loading())
        var localPosts: List<PostWithoutDetails> = emptyList()
        try {
            localPosts = MongoSync.readSponsoredPostsSync().map { it.toPostWithoutDetails() }
            if (localPosts.isNotEmpty()) {
                emit(RequestState.Loading(localPosts))
            }
        } catch (t: Throwable) {
        }

        try {
            val posts = withTimeout(120000) {
                apiService.getSponsoredPosts()
            }
            MongoSync.deleteSponsoredPosts()
            MongoSync.insertPosts(posts.map { it.toLocal().apply { sponsored = true } })
            emit(RequestState.Success(posts))
        } catch (t: Throwable) {
            if (localPosts.isNotEmpty()) {
                emit(RequestState.Success(localPosts))
            } else {
                emit(RequestState.Error(t))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getPopularPosts(skip: Int): Flow<RequestState<List<PostWithoutDetails>>> = flow {
        emit(RequestState.Loading())
        var localPosts: List<PostWithoutDetails> = emptyList()
        try {
            if (skip == 0) {
                localPosts = MongoSync.readPopularPostsSync().map { it.toPostWithoutDetails() }
                if (localPosts.isNotEmpty()) {
                    emit(RequestState.Loading(localPosts))
                }
            }
        } catch (t: Throwable) {
        }

        try {
            val posts = withTimeout(120000) {
                apiService.getPopularPosts(skip)
            }
            if (skip == 0) {
                MongoSync.deletePopularPosts()
            }
            MongoSync.insertPosts(posts.map { it.toLocal().apply { popular = true } })
            emit(RequestState.Success(posts))
        } catch (t: Throwable) {
            if (skip == 0 && localPosts.isNotEmpty()) {
                emit(RequestState.Success(localPosts))
            } else {
                emit(RequestState.Error(t))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun searchPosts(query: String, skip: Int): Flow<RequestState<List<PostWithoutDetails>>> = flow {
        emit(RequestState.Loading())
        try {
            val posts = withTimeout(120000) {
                apiService.searchPostsByTitle(query, skip)
            }
            emit(RequestState.Success(posts))
        } catch (e: Exception) {
            emit(RequestState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getPostById(postId: String): Flow<RequestState<Post>> = flow {
        emit(RequestState.Loading())
        var localPost: LocalPost? = null
        try {
            localPost = MongoSync.readPostByIdSync(postId)
            if (localPost != null) {
                emit(RequestState.Loading(localPost.toPost()))
            }
        } catch (t: Throwable) {
        }

        try {
            val post = withTimeout(120000) {
                apiService.getSelectedPost(postId)
            }
            if (post != null) {
                MongoSync.insertPost(post.toLocal())
                emit(RequestState.Success(post))
            } else {
                MongoSync.deletePost(postId)
                emit(RequestState.Error(Exception("Post not found")))
            }
        } catch (t: Throwable) {
            if (localPost != null) {
                emit(RequestState.Success(localPost.toPost()))
            } else {
                emit(RequestState.Error(t))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun addPost(post: Post): Flow<RequestState<Boolean>> = flow {
        emit(RequestState.Loading())
        try {
            val success = withTimeout(120000) {
                apiService.addPost(post)
            }
            emit(RequestState.Success(success))
        } catch (e: Exception) {
            emit(RequestState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
