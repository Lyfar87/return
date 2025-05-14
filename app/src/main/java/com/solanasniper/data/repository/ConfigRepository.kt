package com.solanasniper.data.repository

import com.solanasniper.domain.model.SnipeConfig

interface ConfigRepository {
    suspend fun getActiveConfigs(): List<SnipeConfig>
    suspend fun saveConfig(config: SnipeConfig)
    suspend fun deleteConfig(id: Int)
    suspend fun getConfigById(id: Int): SnipeConfig?
}