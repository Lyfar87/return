package com.solanasniper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.solanasniper.R
import com.solanasniper.data.model.WalletState

@Composable
fun WalletConnector(
    walletState: WalletState,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.AccountBalanceWallet
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (walletState) {
                is WalletState.Connected -> ConnectedWalletInfo(
                    walletAddress = walletState.address,
                    balance = walletState.balance,
                    onDisconnect = { /* TODO */ }
                )

                is WalletState.Connecting -> ConnectionInProgress()

                is WalletState.Disconnected -> ConnectWalletButton(
                    onConnectClick = onConnectClick,
                    icon = icon
                )

                is WalletState.Error -> ConnectionError(
                    errorMessage = walletState.message,
                    onRetry = onConnectClick
                )
            }
        }
    }
}

@Composable
private fun ConnectWalletButton(
    onConnectClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onConnectClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.connect_wallet))
    }
}

@Composable
private fun ConnectedWalletInfo(
    walletAddress: String,
    balance: Double,
    onDisconnect: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Connected: ${walletAddress.take(6)}...${walletAddress.takeLast(4)}",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onDisconnect) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Disconnect"
                )
            }
        }
        Text(
            text = "Balance: ◎${"%.2f".format(balance)}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ConnectionInProgress() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
        Spacer(Modifier.width(8.dp))
        Text("Connecting...")
    }
}

@Composable
private fun ConnectionError(errorMessage: String, onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Error: $errorMessage",
            color = MaterialTheme.colorScheme.error
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try Again")
        }
    }
}