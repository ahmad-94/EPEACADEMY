package com.example.easypeasyenglish.utils

import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.ApiResponse
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Newsletter
import com.example.easypeasyenglish.models.Params.CAT_PARAM
import com.example.easypeasyenglish.models.Params.POST_ID_PARAM
import com.example.easypeasyenglish.models.Params.QUERY_PARAM
import com.example.easypeasyenglish.models.Params.SKIP_PARAM
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.models.User
import com.example.easypeasyenglish.models.UserWithoutPassword
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.browser.tryPost
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ErrorResponse(val message: String, val debug: String? = null)

suspend fun checkUserExistence(user: User): UserWithoutPassword? {
    return try {
        val result = window.api.tryPost(
            apiPath = "userCheck",
            body = Json.encodeToString(user).encodeToByteArray()
        )

        val responseText = result?.decodeToString() ?: return null
        val json = Json { ignoreUnknownKeys = true }

        return if ("message" in responseText) {
            val error = json.decodeFromString<ErrorResponse>(responseText)
            println("Error: ${error.message}")
            error.debug?.let { println("Server debug: $it") }
            null
        } else {
            println("API response: $responseText")
//            json.decodeFromString<UserWithoutPassword>(responseText)
            responseText.parseData()
        }
    } catch (e: Exception) {
        println("Exception: ${e.message}")
        null
    }
}

suspend fun checkUserId(id: String): Boolean {
    return try {
        val result = window.api.tryPost(
            apiPath = "checkUserId",
            body = Json.encodeToString(id).encodeToByteArray()
        )
        result?.decodeToString()?.let { Json.decodeFromString<Boolean>(it)  } ?: return false
    } catch (e: Exception) {
        println("Check user ID: ${e.message}")
        false
    }
}

suspend fun addPost(post: Post): Boolean {
    return try {
        val text = window.api.tryPost(
            apiPath = "addpost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString() ?: false
        Json.decodeFromString<Boolean>(text as String)
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun fetchMyPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readmyposts?skip=$skip&author=${localStorage.getItem("username")}"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun fetchMainPosts(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try  {
        val mainPosts = window.api.tryGet(apiPath = "readMainPosts")?.decodeToString()
        print(mainPosts)
        onSuccess(mainPosts.parseData())
        println(mainPosts)
    } catch (e: Exception) {
        onError(e)
        println(e)
    }
}

suspend fun fetchLatestPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try  {
        val latestPosts = window.api.tryGet(apiPath = "readLatestPosts?skip=$skip")?.decodeToString()
        onSuccess(latestPosts.parseData())
    } catch (e: Exception) {
        onError(e)
        println(e)
    }
}

suspend fun fetchSponsoredPosts(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try  {
        val latestPosts = window.api.tryGet(apiPath = "readSponsoredPosts")?.decodeToString()
        onSuccess(latestPosts.parseData())
    } catch (e: Exception) {
        onError(e)
        println(e)
    }
}

suspend fun fetchPopularPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try  {
        val popularPosts = window.api.tryGet(apiPath = "readPopularPosts?skip=$skip")?.decodeToString()
        onSuccess(popularPosts.parseData())
    } catch (e: Exception) {
        onError(e)
        println(e)
    }
}

suspend fun deleteSelectedPosts(selectedPosts: List<String>): Boolean {
    return try {
        val result = window.api.tryPost(
            apiPath = "deleteSelectedPosts",
            body = Json.encodeToString(selectedPosts).encodeToByteArray()
        )?.decodeToString() ?: false
        Json.decodeFromString(result as String)
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun searchPostsByTitle(
    query: String,
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    return try {
        val result = window.api.tryGet(
            apiPath = "searchPostsByTitle?${QUERY_PARAM}=$query&${SKIP_PARAM}=$skip"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun searchPostsByCategory(
    category: Category,
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    return try {
        val result = window.api.tryGet(
            apiPath = "searchPostsByCategory?$CAT_PARAM=${category.name}&${SKIP_PARAM}=$skip"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun fetchSelectedPost(id: String): ApiResponse {
    try {
        val result = window.api.tryGet(
            apiPath = "readSelectedPost?$POST_ID_PARAM=$id"
        )?.decodeToString()
        return result?.parseData() ?: ApiResponse.Error(message = "Result is null!")

    } catch (e: Exception) {
        return ApiResponse.Error(e.message.toString())
    }
}

suspend fun updatePost(post: Post): Boolean {
    return try {
        val text = window.api.tryPost(
            apiPath = "updatePost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString() ?: false
        Json.decodeFromString(text as String)

    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun subscribeToNewsLetter(newsletter: Newsletter): String {

        return window.api.tryPost(
            apiPath = "subscribe",
            body = Json.encodeToString(newsletter).encodeToByteArray()
        )?.decodeToString().toString().replace("\"", "")

}

inline fun <reified T> String?.parseData(): T {
    return Json.decodeFromString(this.toString())
}