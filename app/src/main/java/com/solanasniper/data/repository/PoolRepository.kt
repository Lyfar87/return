package com.solanasniper.data.repository

import com.solanasniper.data.api.BirdeyeApi
import com.solanasniper.data.database.AppDatabase
import com.solanasniper.data.model.Pool
import com.solanasniper.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PoolRepository @Inject constructor(
    private val api: BirdeyeApi,
    private val database: AppDatabase
) {
    private val poolDao = database.poolDao()

    suspend fun getNewPools(): List<Pool> {
        return try {
            // 1. Получить данные из API
            val apiPools = api.getNewPools().data ?: emptyList()

            // 2. Сохранить в базу данных
            poolDao.insertAll(apiPools)

            // 3. Вернуть объединенные данные
            poolDao.getAllPools()
        } catch (e: Exception) {
            // При ошибке вернуть кэшированные данные
            poolDao.getAllPools()
        }
    }

    fun observePools(): Flow<NetworkResult<List<Pool>>> = flow {
        try {
            // 1. Отправить состояние загрузки
            emit(NetworkResult.Loading)

            // 2. Получить актуальные данные
            val pools = withContext(Dispatchers.IO) {
                api.getNewPools().data ?: emptyList()
            }

            // 3. Обновить кэш
            poolDao.insertAll(pools)

            // 4. Отправить успешный результат
            emit(NetworkResult.Success(poolDao.getAllPools()))
        } catch (e: Exception) {
            // 5. Обработать ошибку
            emit(NetworkResult.Error(
                message = "Failed to load pools: ${e.message}",
                data = poolDao.getAllPools()
            ))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun trackPool(pool: Pool) {
        withContext(Dispatchers.IO) {
            poolDao.update(pool.copy(isTracked = true))
        }
    }

    suspend fun getTrackedPools(): List<Pool> {
        return withContext(Dispatchers.IO) {
            poolDao.getTrackedPools()
        }
    }

    suspend fun getPoolByAddress(address: String): Pool? {
        return withContext(Dispatchers.IO) {
            poolDao.getPoolByAddress(address)
        }
    }
}