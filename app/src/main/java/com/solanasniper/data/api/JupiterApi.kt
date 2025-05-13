package com.solanasniper.data.api

import com.solanasniper.data.model.JupiterQuoteResponse
import com.solanasniper.data.model.JupiterSwapResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface JupiterApi {
    /**
     * Получение лучшей котировки для свопа
     * @param inputMint Адрес входного токена (например, SOL)
     * @param outputMint Адрес выходного токена
     * @param amount Сумма в минимальных единицах (lamports)
     * @param slippageBps Допустимое проскальзывание (в basis points, 1 = 0.01%)
     */
    @GET("v6/quote")
    suspend fun getQuote(
        @Query("inputMint") inputMint: String,
        @Query("outputMint") outputMint: String,
        @Query("amount") amount: Long,
        @Query("slippageBps") slippageBps: Int = 100, // 1% по умолчанию
        @Query("feeBps") feeBps: Int? = null, // Комиссия платформы
        @Header("Authorization") apiKey: String? = null
    ): Response<JupiterQuoteResponse>

    /**
     * Создание транзакции для свопа
     * @param request Параметры свопа из котировки
     */
    @POST("v6/swap")
    suspend fun createSwapTransaction(
        @Body request: JupiterSwapRequest,
        @Header("Authorization") apiKey: String? = null
    ): Response<JupiterSwapResponse>

    companion object {
        const val BASE_URL = "https://quote-api.jup.ag/"
        const val FEE_ACCOUNT = "7Z36Efbt7a4hT6Z36Efbt7a4hT6...YOUR_FEE_ACCOUNT" // Опционально
    }
}

// Модели данных
data class JupiterQuoteResponse(
    val inputMint: String,
    val outputMint: String,
    val inAmount: Long,
    val outAmount: Long,
    val priceImpactPct: Double,
    val routes: List<JupiterRoute>,
    val swapMode: String = "ExactIn"
)

data class JupiterRoute(
    val marketInfos: List<JupiterMarketInfo>,
    val outAmount: Long,
    val slippageBps: Int
)

data class JupiterMarketInfo(
    val id: String,
    val label: String, // Например: "Raydium"
    val inputMint: String,
    val outputMint: String,
    val inAmount: Long,
    val outAmount: Long,
    val lpFee: JupiterFee,
    val platformFee: JupiterFee?
)

data class JupiterFee(
    val amount: Long,
    val mint: String,
    val pct: Double
)

data class JupiterSwapRequest(
    val quoteResponse: JupiterQuoteResponse,
    val userPublicKey: String,
    val feeAccount: String? = JupiterApi.FEE_ACCOUNT,
    val wrapUnwrapSOL: Boolean = true
)

data class JupiterSwapResponse(
    val swapTransaction: String, // Base64-encoded transaction
    val lastValidBlockHeight: Long,
    val prioritizationFeeLamports: Long = 5000 // Lamports
)