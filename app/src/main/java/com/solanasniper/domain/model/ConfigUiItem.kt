package com.solanasniper.domain.model

data class ConfigUiItem(
    val id: Int,
    val tokenAddress: String,
    val dexType: String,
    val amount: Double,
    val slippage: Double,
    val isActive: Boolean
)