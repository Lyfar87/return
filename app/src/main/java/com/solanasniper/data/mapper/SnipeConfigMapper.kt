package com.solanasniper.data.mapper

import com.solanasniper.data.model.SnipeConfigEntity
import com.solanasniper.domain.model.SnipeConfig

fun SnipeConfigEntity.toDomainModel(): SnipeConfig = SnipeConfig(
    tokenAddress = tokenAddress,
    buyPrice = buyPrice,
    dexType = SnipeConfig.DexType.valueOf(dexType),
    amount = amount,
    slippage = slippage,
    stopLossPercent = stopLossPercent,
    takeProfitPercent = takeProfitPercent,
    isActive = isActive,
    createdAt = createdAt,
    lastUpdated = updatedAt
)

fun SnipeConfig.toEntity(): SnipeConfigEntity = SnipeConfigEntity(
    tokenAddress = tokenAddress,
    buyPrice = buyPrice,
    dexType = dexType.name,
    amount = amount,
    slippage = slippage,
    stopLossPercent = stopLossPercent,
    takeProfitPercent = takeProfitPercent,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = lastUpdated
)