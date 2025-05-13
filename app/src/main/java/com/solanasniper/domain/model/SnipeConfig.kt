package com.solanasniper.domain.model

import java.util.Date

data class SnipeConfig(
    val tokenAddress: String,
    val buyPrice: Double,
    val stopLossPercent: Double,
    val takeProfitPercent: Double? = null,
    val dexType: DexType = DexType.RAYDIUM,
    val amount: Double,
    val slippage: Double = 1.0,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val lastUpdated: Date = Date()
) {
    enum class DexType { RAYDIUM, JUPITER, ORCA }

    init {
        require(stopLossPercent > 0) { "Stop-Loss must be positive" }
        takeProfitPercent?.let { require(it > 0) { "Take-Profit must be positive" } }
        require(slippage in 0.1..100.0) { "Invalid slippage: $slippage%" }
    }

    fun calculateStopLossPrice(): Double {
        return buyPrice * (1 - stopLossPercent / 100)
    }

    fun calculateTakeProfitPrice(): Double? {
        return takeProfitPercent?.let { buyPrice * (1 + it / 100) }
    }

    fun checkTriggers(currentPrice: Double): TriggerResult {
        return when {
            currentPrice <= calculateStopLossPrice() -> TriggerResult.STOP_LOSS
            takeProfitPercent != null && currentPrice >= calculateTakeProfitPrice()!! ->
                TriggerResult.TAKE_PROFIT
            else -> TriggerResult.NONE
        }
    }

    fun toEntity() = SnipeConfigEntity(
        tokenAddress = tokenAddress,
        buyPrice = buyPrice,
        stopLossPercent = stopLossPercent,
        takeProfitPercent = takeProfitPercent,
        dexType = dexType.name,
        amount = amount,
        slippage = slippage,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = lastUpdated
    )

    sealed class TriggerResult {
        object STOP_LOSS : TriggerResult()
        object TAKE_PROFIT : TriggerResult()
        object NONE : TriggerResult()
    }
}