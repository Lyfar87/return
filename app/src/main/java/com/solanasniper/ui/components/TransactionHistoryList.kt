package com.solanasniper.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.solanasniper.domain.model.Transaction

@Composable
fun TransactionHistoryList(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(transactions, key = { it.hash }) { transaction ->
            TransactionItem(
                hash = transaction.hash,
                status = transaction.status,
                timestamp = transaction.timestamp
            )
        }
    }
}

@Composable
private fun TransactionItem(
    hash: String,
    status: TransactionStatus,
    timestamp: Instant
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TX: ${hash.take(6)}...${hash.takeLast(4)}")
            Text("Status: ${status.name}")
            Text("Time: ${timestamp.toLocalDateTime()}")
        }
    }
}