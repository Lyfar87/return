package com.solanasniper.data.network

import android.util.Log
import com.solanasniper.BuildConfig
import com.solanasniper.data.api.SolanaRpcApi
import com.solanasniper.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RpcManager {
    private const val TAG = "RpcManager"

    // Основные RPC-провайдеры
    private val defaultRpcs = listOf(
        RpcConfig(
            name = "Jito",
            url = "https://jito-mainnet.rpcpool.com",
            apiKey = BuildConfig.JITO_API_KEY
        ),
        RpcConfig(
            name = "Helius",
            url = "https://mainnet.helius-rpc.com",
            apiKey = BuildConfig.HELIUS_API_KEY
        )
    )

    private val customRpcs = mutableListOf<RpcConfig>()
    private var currentRpc: RpcConfig = defaultRpcs.first()

    data class RpcConfig(
        val name: String,
        val url: String,
        val apiKey: String? = null
    ) {
        fun getFullUrl() = apiKey?.let { "$url?api-key=$it" } ?: url
    }

    // Получение текущего RPC URL
    fun getCurrentRpcUrl(): String = currentRpc.getFullUrl()

    // Получение списка всех доступных нод
    fun getAvailableRpcs(): List<RpcConfig> = defaultRpcs + customRpcs

    // Установка кастомной RPC ноды
    fun setCustomRpc(name: String, url: String, apiKey: String? = null) {
        require(NetworkUtils.isValidUrl(url)) { "Invalid RPC URL" }

        customRpcs.removeAll { it.url == url }
        customRpcs.add(RpcConfig(name, url, apiKey))
        currentRpc = customRpcs.last()
    }

    // Переключение на другую ноду
    fun switchRpc(config: RpcConfig) {
        currentRpc = config
        Log.i(TAG, "Switched to ${config.name} RPC")
    }

    // Проверка работоспособности ноды
    suspend fun checkRpcHealth(url: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("$url/health")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Health check failed: ${e.localizedMessage}")
            false
        }
    }

    // Инициализация Retrofit клиента
    fun createApiClient(): SolanaRpcApi {
        return Retrofit.Builder()
            .baseUrl(getCurrentRpcUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(createHttpClient())
            .build()
            .create(SolanaRpcApi::class.java)
    }

    private fun createHttpClient() = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()
}