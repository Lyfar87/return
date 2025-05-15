package com.solanasniper.data.dao

import androidx.room.*
import com.solanasniper.data.model.SnipeConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    // Добавление новой конфигурации
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: SnipeConfigEntity)

    // Удаление конфигурации по ID
    @Query("DELETE FROM snipe_configs WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Получение всех активных конфигураций
    @Query("SELECT * FROM snipe_configs WHERE isActive = 1 ORDER BY createdAt DESC")
    suspend fun getActiveConfigs(): List<SnipeConfigEntity>

    // Получение конфигурации по ID
    @Query("SELECT * FROM snipe_configs WHERE id = :id")
    suspend fun getConfigById(id: Int): SnipeConfigEntity?
}