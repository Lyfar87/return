package com.solanasniper.data.network

import com.solanasniper.data.model.RpcRequest
import com.solanasniper.data.model.RpcResponse
import com.solanasniper.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

interface SolanaRpcService {
    @POST("/")
    suspend fun executeRequest(@Body request: RpcRequest): Response<RpcResponse>
}

class SolanaRpcClient @Inject constructor() {
    private val service: SolanaRpcService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mainnet-beta.solana.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolanaRpcService::class.java)
    }

    suspend fun getRecentBlockhash(): NetworkResult<String> = try {
        val response = service.executeRequest(
            RpcRequest(
                method = "getRecentBlockhash",
                params = emptyList()
            )
        )
        parseBlockhashResponse(response)
    } catch (e: Exception) {
        NetworkResult.Error("RPC Error: ${e.message}")
    }

    private fun parseBlockhashResponse(response: Response<RpcResponse>): NetworkResult<String> {
        return if (response.isSuccessful) {
            response.body()?.result?.value?.blockhash?.let {
                NetworkResult.Success(it)
            } ?: NetworkResult.Error("Invalid response format")
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    // Добавьте другие методы: getBalance, sendTransaction и т.д.
}