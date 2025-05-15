package com.solanasniper.data.repository

import com.solanasniper.data.mapper.toDomainModel
import com.solanasniper.data.mapper.toEntity
import com.solanasniper.data.dao.ConfigDao
import com.solanasniper.domain.model.SnipeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val configDao: ConfigDao
) : ConfigRepository {

    override suspend fun getActiveConfigs(): List<SnipeConfig> = withContext(Dispatchers.IO) {
        configDao.getActiveConfigs().map { it.toDomainModel() }
    }

    override suspend fun saveConfig(config: SnipeConfig) = withContext(Dispatchers.IO) {
        configDao.insertOrUpdate(config.toEntity())
    }

    override suspend fun deleteConfig(id: Int) = withContext(Dispatchers.IO) {
        configDao.deleteById(id)
    }

    override suspend fun getConfigById(id: Int): SnipeConfig? = withContext(Dispatchers.IO) {
        configDao.getConfigById(id)?.toDomainModel()
    }
}