package com.example.easypeasyenglish.data

import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Newsletter
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.models.User

interface MongoRepository {
    suspend fun addPost(post: Post): Boolean
    suspend fun updatePost(post: Post): Boolean
    suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails>
    suspend fun readMainPosts(): List<PostWithoutDetails>
    suspend fun readLatestPosts(skip: Int): List<PostWithoutDetails>
    suspend fun readAllPosts(skip: Int): List<PostWithoutDetails>
    suspend fun readSponsoredPosts(): List<PostWithoutDetails>
    suspend fun readPopularPosts(skip: Int): List<PostWithoutDetails>
    suspend fun searchPostsByTitle(query: String, skip: Int): List<PostWithoutDetails>
    suspend fun searchPostsByCategory(category: Category, skip: Int): List<PostWithoutDetails>
    suspend fun readSelectedPost(id: String): Post
    suspend fun deleteSelectedPosts(selectedPosts: List<String>): Boolean
    suspend fun checkUserExistence(user: User): User?
    suspend fun checkUserId(id: String): Boolean

    suspend fun subscribe(newsletter: Newsletter): String
}