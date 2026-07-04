package com.example.androidapp.network

import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.ApiResponse
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiService(
    private val baseUrl: String = "https://epeacademy.onrender.com"
) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json, contentType = ContentType.Any)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120000
            connectTimeoutMillis = 120000
            socketTimeoutMillis = 120000
        }
        defaultRequest {
            header("Content-Type", "application/json")
        }
    }

    private suspend fun <T> safeDecode(response: HttpResponse, mapper: (String) -> T): T {
        val body = response.bodyAsText()
        if (body.isEmpty()) {
            throw Exception("Empty response from server (Status: ${response.status})")
        }
        return try {
            mapper(body)
        } catch (e: Exception) {
            throw Exception("Serialization Error: ${e.message}. Body: $body")
        }
    }

    suspend fun getMainPosts(): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/readMainPosts")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    suspend fun getLatestPosts(skip: Int): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/readLatestPosts?skip=$skip")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    suspend fun getAllPosts(skip: Int): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/readAllPosts?skip=$skip")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    suspend fun getSponsoredPosts(): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/readSponsoredPosts")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    suspend fun getPopularPosts(skip: Int): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/readPopularPosts?skip=$skip")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    suspend fun getSelectedPost(postId: String): Post? {
        val response = client.get {
            url("$baseUrl/api/readSelectedPost?postId=$postId")
        }
        return safeDecode(response) {
            when (val responseData = json.decodeFromString<ApiResponse>(it)) {
                is ApiResponse.Success -> responseData.data
                is ApiResponse.Error -> throw Exception(responseData.message)
                else -> null
            }
        }
    }

    suspend fun searchPostsByTitle(query: String, skip: Int): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/searchPostsByTitle?query=$query&skip=$skip")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    suspend fun searchPostsByCategory(category: Category, skip: Int): List<PostWithoutDetails> {
        val response = client.get {
            url("$baseUrl/api/searchPostsByCategory?category=${category.name}&skip=$skip")
        }
        return safeDecode(response) { handleListResponse(json.decodeFromString<ApiListResponse>(it)) }
    }

    private fun handleListResponse(response: ApiListResponse): List<PostWithoutDetails> {
        return when (response) {
            is ApiListResponse.Success -> response.data
            is ApiListResponse.Error -> throw Exception(response.message)
            else -> emptyList()
        }
    }

    suspend fun addPost(post: Post): Boolean {
        return client.post {
            url("$baseUrl/api/addpost")
            contentType(ContentType.Application.Json)
            setBody(post)
        }.bodyAsText().toBoolean()
    }

    suspend fun updatePost(post: Post): Boolean {
        return client.post {
            url("$baseUrl/api/updatePost")
            contentType(ContentType.Application.Json)
            setBody(post)
        }.bodyAsText().toBoolean()
    }

    suspend fun deleteSelectedPosts(postIds: List<String>): Boolean {
        return client.post {
            url("$baseUrl/api/deleteSelectedPosts")
            contentType(ContentType.Application.Json)
            setBody(postIds)
        }.bodyAsText().toBoolean()
    }

    suspend fun checkUserExistence(user: User): User? {
        val response = client.post {
            url("$baseUrl/api/userCheck")
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        
        return try {
            json.decodeFromString<User>(response.bodyAsText())
        } catch (e: Exception) {
            null
        }
    }

    suspend fun subscribeToNewsletter(email: String): String {
        return client.post {
            url("$baseUrl/api/subscribe")
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email))
        }.bodyAsText()
    }
}
