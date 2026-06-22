package com.example.easypeasyenglish.api

import com.example.easypeasyenglish.data.MongoDB
import com.example.easypeasyenglish.data.MongoRepository
import com.example.easypeasyenglish.models.User
import com.example.easypeasyenglish.models.UserWithoutPassword
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


@Serializable
data class ErrorResponse(val message: String, val debug: String? = null)

@Api("userCheck")
suspend fun userCheck(context: ApiContext) {
    try {
        val userRequest = context.req.body?.decodeToString()?.let {
            Json.decodeFromString<User>(it)
        }

        val user = userRequest?.let {
            val hashed = hashPassword(it.password)
            context.logger.info("🔒 Input password: ${it.password}")
            context.logger.info("🔑 Hashed password: $hashed")

            context.data.getValue<MongoRepository>().checkUserExistence(
                User(username = it.username, password = hashed)
            )
        }

        if (user != null) {
            context.res.setBodyText(
                Json.encodeToString(
                    UserWithoutPassword(id = user.id, username = user.username)
                )
            )
        } else {
            val error = ErrorResponse(
                message = "User doesn't exist!",
                debug = "Checked user: ${userRequest?.username} with hashed password: ${hashPassword(userRequest?.password ?: "")}"
            )
            context.res.setBodyText(Json.encodeToString(error))
        }
    } catch (e: Exception) {
        val error = ErrorResponse(
            message = e.message ?: "Unknown server error",
            debug = e.stackTraceToString()
        )
        context.res.setBodyText(Json.encodeToString(error))
    }
}

@Api("checkUserId")
suspend fun checkUserId(context: ApiContext) {
   try {
       val idRequest =
           context.req.body?.decodeToString()?.let { Json.decodeFromString<String>(it) }
       val result = idRequest?.let {
           context.data.getValue<MongoRepository>().checkUserId(it)
       }
       if (result != null) {
           context.res.setBodyText(Json.encodeToString(result))
       } else {
           context.res.setBodyText(Json.encodeToString(false))
       }
   } catch (e: Exception) {
       println("Check user Id: ${e.message}")
       context.res.setBodyText(Json.encodeToString(false))
   }
}


private fun hashPassword(password: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
    val hexString = StringBuffer()

    for (byte in hashBytes) {
        hexString.append(String.format("%02x", byte))
    }

    return hexString.toString()
}


