package com.aura.assistant.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@Serializable
data class AssistantRequest(
    val query: String,
    val assistant_name: String,
    val personality: String,
    val language: String
)

@Serializable
data class AssistantResponse(
    val response: String,
    val tokens_evaluated: Int,
    val processing_time_ms: Double
)

interface AuraBackendService {
    @POST("v1/assistant/chat")
    suspend fun chat(
        @Header("Authorization") token: String,
        @Body request: AssistantRequest
    ): AssistantResponse
}
