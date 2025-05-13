package com.solanasniper.data.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Данные для вызова API Raydium (адаптируйте под конкретные эндпоинты)
data class RaydiumPool(
    val id: String,
    val name: String,
    val liquidity: Double,
    val createdAt: String,
    val dex: String
)

data class RaydiumResponse(
    val pools: List<RaydiumPool>
)

interface RaydiumApi {

    // Пример метода для получения пулов
    @GET("v2/pools")
    suspend fun getPools(
        @Query("limit") limit: Int = 50,
        @Query("min_liquidity") minLiquidity: Double? = null
    ): Response<RaydiumResponse>

    companion object {
        private const val BASE_URL = "https://api.raydium.io/"

        fun create(): RaydiumApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RaydiumApi::class.java)
        }
    }
}