package com.solanasniper.data.api

import com.solanasniper.data.model.BirdeyePoolResponse
import com.solanasniper.data.model.BirdeyePriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BirdeyeApi {
    /**
     * Получение списка новых пулов ликвидности
     * @param sortBy Сортировка (по умолчанию - время создания)
     * @param limit Количество записей
     * @param apiKey API-ключ Birdeye
     */
    @GET("public/tokenlist")
    suspend fun getNewPools(
        @Query("sortBy") sortBy: String = "created",
        @Query("limit") limit: Int = 50,
        @Header("X-API-KEY") apiKey: String
    ): Response<BirdeyePoolResponse>

    /**
     * Получение текущей цены токена
     * @param address Адрес токена
     * @param apiKey API-ключ Birdeye
     */
    @GET("public/price")
    suspend fun getTokenPrice(
        @Query("address") address: String,
        @Header("X-API-KEY") apiKey: String
    ): Response<BirdeyePriceResponse>

    companion object {
        const val BASE_URL = "https://public-api.birdeye.so/"
    }
}

// Модели данных
data class BirdeyePoolResponse(
    val success: Boolean,
    val data: List<BirdeyePool>
)

data class BirdeyePool(
    val id: String,
    val mint: String,
    val name: String,
    val symbol: String,
    val liquidity: Double,
    val createdUnixTime: Long
)

data class BirdeyePriceResponse(
    val success: Boolean,
    val data: TokenPriceData
)

data class TokenPriceData(
    val value: Double,
    val updateUnixTime: Long
)