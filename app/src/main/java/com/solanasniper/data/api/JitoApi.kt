package com.solanasniper.data.api

import com.solanasniper.data.model.JitoBundleResponse
import com.solanasniper.data.model.JitoBundleStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface JitoApi {
    /**
     * Отправка транзакционного бандла
     * @param auth Jito API Key в формате "Bearer <key>"
     * @param bundle Запрос с транзакциями
     */
    @POST("v1/bundle")
    suspend fun sendBundle(
        @Header("Authorization") auth: String,
        @Body bundle: JitoBundleRequest
    ): Response<JitoBundleResponse>

    /**
     * Проверка статуса бандла
     * @param bundleId UUID бандла
     * @param auth Jito API Key
     */
    @GET("v1/bundle/{bundleId}")
    suspend fun getBundleStatus(
        @Path("bundleId") bundleId: String,
        @Header("Authorization") auth: String
    ): Response<JitoBundleStatus>

    companion object {
        const val BASE_URL = "https://api.jito.network/" // Актуальный URL
    }
}

// Модели запроса/ответа
data class JitoBundleRequest(
    val transactions: List<String>, // Base64-encoded transactions
    val fee: Long, // Lamports
    val priorityFee: Long = 0, // Optional priority fee
    val blockhash: String? = null // Latest blockhash
)

data class JitoBundleResponse(
    val bundleId: String,
    val status: String, // PENDING/SUCCESS/FAILED
    val message: String? = null
)

data class JitoBundleStatus(
    val state: String, // PROCESSED/DROPPED
    val slot: Long,
    val error: String? = null
)