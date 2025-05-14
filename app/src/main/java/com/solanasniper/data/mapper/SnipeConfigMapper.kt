package com.solanasniper.data.mapper

import com.solanasniper.data.entity.SnipeConfigEntity
import com.solanasniper.domain.model.SnipeConfig

fun SnipeConfigEntity.toDomain(): SnipeConfig = SnipeConfig(
    id = id,
    tokenAddress = tokenAddress,
    buyPrice = buyPrice,
    stopLossPercent = stopLossPercent,
    dexType = dexType,
    isActive = isActive,
    createdAt = createdAt
)

fun SnipeConfig.toEntity(): SnipeConfigEntity = SnipeConfigEntity(
    id = id,
    tokenAddress = tokenAddress,
    buyPrice = buyPrice,
    stopLossPercent = stopLossPercent,
    dexType = dexType,
    isActive = isActive,
    createdAt = createdAt
)