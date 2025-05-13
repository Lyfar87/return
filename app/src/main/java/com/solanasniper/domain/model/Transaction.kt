package com.solanasniper.domain.model

import java.time.Instant

enum class TransactionStatus { PENDING, CONFIRMED, FAILED }

data class Transaction(
    val hash: String,
    val status: TransactionStatus,
    val amountLamports: Long,
    val timestamp: Instant = Instant.now()
)