package com.example.easypeasyenglish.models

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


@Serializable(ApiListResponseSerializer::class)
sealed class ApiListResponse {
    @Serializable
    @SerialName("idle")
    object Idle: ApiListResponse()

    @Serializable
    @SerialName("success")
    class Success(val data: List<PostWithoutDetails>): ApiListResponse()

    @Serializable
    @SerialName("error")
    class Error(val message: String): ApiListResponse()
}

@Serializable(ApiResponseSerializer::class)
sealed class ApiResponse {
    @Serializable
    @SerialName("idle")
    object Idle: ApiResponse()

    @Serializable
    @SerialName("success")
    class Success(val data: Post): ApiResponse()

    @Serializable
    @SerialName("error")
    class Error(val message: String): ApiResponse()
}

object ApiListResponseSerializer:
    JsonContentPolymorphicSerializer<ApiListResponse>(ApiListResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ApiListResponse> {
        val result = when {
            "data" in element.jsonObject -> ApiListResponse.Success.serializer()
            "message" in element.jsonObject -> ApiListResponse.Error.serializer()
            else -> ApiListResponse.Idle.serializer()
        }
        return result
    }
}

object ApiResponseSerializer:
    JsonContentPolymorphicSerializer<ApiResponse>(ApiResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ApiResponse> {
        val result = when {
            "data" in element.jsonObject -> ApiResponse.Success.serializer()
            "message" in element.jsonObject -> ApiResponse.Error.serializer()
            else -> ApiResponse.Idle.serializer()
        }
        return result
    }
}

