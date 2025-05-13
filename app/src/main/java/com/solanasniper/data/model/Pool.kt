package com.solanasniper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "pools")
data class Pool(
    @PrimaryKey
    @SerializedName("id")
    val poolAddress: String,

    @SerializedName("base_mint")
    val baseMint: String,

    @SerializedName("quote_mint")
    val quoteMint: String,

    @SerializedName("name")
    val pairName: String,

    @SerializedName("symbol")
    val pairSymbol: String,

    @SerializedName("liquidity_usd")
    val liquidityUSD: Double,

    @SerializedName("price")
    val currentPrice: Double,

    @SerializedName("volume_24h")
    val volume24h: Double,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("dex")
    val dexType: String,

    val lastUpdated: Date = Date(),

    var isTracked: Boolean = false
) {
    fun isNewPool(thresholdMinutes: Int = 5): Boolean {
        val diff = (Date().time - createdAt.time) / 60000
        return diff < thresholdMinutes
    }

    fun formattedLiquidity(): String {
        return when {
            liquidityUSD >= 1_000_000 -> "$${"%.1fM".format(liquidityUSD / 1_000_000)}"
            liquidityUSD >= 1_000 -> "$${"%.1fK".format(liquidityUSD / 1_000)}"
            else -> "$${"%.0f".format(liquidityUSD)}"
        }
    }
}