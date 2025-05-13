package com.solanasniper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.solanasniper.data.database.Converters
import java.util.*

@Entity(tableName = "snipe_configs")
@TypeConverters(Converters::class)
data class SnipeConfigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tokenAddress: String,
    val dexType: String,
    val amount: Double,
    val slippage: Double,
    val stopLossPercent: Double,
    val takeProfitPercent: Double?,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun toDomainModel() = com.solanasniper.domain.model.SnipeConfig(
        tokenAddress = tokenAddress,
        dexType = com.solanasniper.domain.model.DexType.valueOf(dexType),
        amount = amount,
        slippage = slippage,
        stopLossPercent = stopLossPercent,
        takeProfitPercent = takeProfitPercent,
        isActive = isActive
    )
}