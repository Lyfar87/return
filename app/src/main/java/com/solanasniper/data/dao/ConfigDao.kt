package com.solanasniper.data.dao

import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    // Добавление новой конфигурации
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: SnipeConfigEntity)

    // Обновление существующей конфигурации
    @Update
    suspend fun update(config: SnipeConfigEntity)

    // Удаление конфигурации
    @Delete
    suspend fun delete(config: SnipeConfigEntity)

    // Получение всех активных конфигураций
    @Query("SELECT * FROM configs WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveConfigs(): Flow<List<SnipeConfigEntity>>

    // Получение конфигурации по ID
    @Query("SELECT * FROM configs WHERE id = :id")
    suspend fun getById(id: Int): SnipeConfigEntity?

    // Получение всех конфигураций (включая неактивные)
    @Query("SELECT * FROM configs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SnipeConfigEntity>>

    // Обновление статуса активности
    @Query("UPDATE configs SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Int, active: Boolean)

    // Поиск по адресу токена
    @Query("SELECT * FROM configs WHERE tokenAddress LIKE :query")
    fun searchByToken(query: String): Flow<List<SnipeConfigEntity>>

    // Удаление всех конфигураций
    @Query("DELETE FROM configs")
    suspend fun deleteAll()
}