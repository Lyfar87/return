package com.solanasniper.data.repository

import com.solanasniper.data.dao.ConfigDao
import com.solanasniper.data.mapper.toDomain
import com.solanasniper.data.mapper.toEntity
import com.solanasniper.domain.model.SnipeConfig

class ConfigRepositoryImpl(
    private val configDao: ConfigDao
) : ConfigRepository {
    override suspend fun getActiveConfigs(): List<SnipeConfig> {
        return configDao.getActiveConfigs().map { it.toDomain() }
    }

    override suspend fun saveConfig(config: SnipeConfig) {
        configDao.insert(config.toEntity())
    }

    override suspend fun deleteConfig(id: Int) {
        configDao.deleteById(id)
    }

    override suspend fun getConfigById(id: Int): SnipeConfig? {
        return configDao.getById(id)?.toDomain()
    }
}