package com.solanasniper.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    @ColumnInfo(name = "tx_id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "tx_hash")
    val hash: String? = null,

    @ColumnInfo(name = "type")
    val type: Type = Type.SWAP,

    @ColumnInfo(name = "status")
    val status: Status = Status.PENDING,

    @ColumnInfo(name = "sender")
    val senderAddress: String,

    @ColumnInfo(name = "receiver")
    val receiverAddress: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "fee")
    val fee: Long, // lamports

    @ColumnInfo(name = "token_address")
    val tokenAddress: String? = null,

    @ColumnInfo(name = "dex_type")
    val dexType: DexType? = null,

    @ColumnInfo(name = "raw_data")
    val rawData: String, // Base64 encoded transaction

    @ColumnInfo(name = "timestamp")
    val timestamp: Date = Date(),

    @ColumnInfo(name = "error")
    val error: String? = null
) {
    enum class Type { SWAP, TRANSFER, STAKE }

    enum class Status {
        PENDING,
        CONFIRMED,
        FAILED,
        TIMED_OUT
    }

    enum class DexType { RAYDIUM, JUPITER, ORCA }

    fun formattedAmount(): String {
        return "◎${String.format("%.4f", amount)}"
    }

    fun formattedFee(): String {
        return "${fee / 1_000_000_000.0} SOL"
    }

    fun isSwapTransaction() = type == Type.SWAP && dexType != null

    companion object {
        fun dummySwap() = Transaction(
            senderAddress = "Alice...",
            receiverAddress = "Raydium Pool",
            amount = 1.5,
            fee = 5000,
            tokenAddress = "EPjFWdd5...",
            dexType = DexType.RAYDIUM,
            rawData = "base64EncodedData..."
        )
    }
}