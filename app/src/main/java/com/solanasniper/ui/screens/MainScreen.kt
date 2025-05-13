package com.solanasniper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solanasniper.R
import com.solanasniper.ui.components.PoolCard
import com.solanasniper.ui.components.WalletConnector
import com.solanasniper.ui.viewmodels.MainViewModel

@Composable
fun MainScreen(
    onSettingsClick: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    Scaffold(
        topBar = {
            MainAppBar(
                onRefresh = viewModel::refreshPools,
                onSettingsClick = onSettingsClick,
                isLoading = uiState.isRefreshing
            )
        },
        content = { padding ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = viewModel::refreshPools,
                modifier = Modifier.padding(padding)
            ) {
                MainContent(
                    uiState = uiState,
                    walletState = uiState.walletState,
                    onConnectWallet = viewModel::connectWallet,
                    onSnipePool = viewModel::snipePool
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppBar(
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    isLoading: Boolean
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            RefreshButton(
                isLoading = isLoading,
                onClick = onRefresh
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
            }
        }
    )
}

@Composable
private fun RefreshButton(isLoading: Boolean, onClick: () -> Unit) {
    val icon = if (isLoading) {
        Icons.Default.Refresh
    } else {
        Icons.Default.Cached
    }

    IconButton(
        onClick = onClick,
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.refresh))
        }
    }
}

@Composable
private fun MainContent(
    uiState: MainUiState,
    walletState: WalletState,
    onConnectWallet: () -> Unit,
    onSnipePool: (Pool) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        WalletConnector(
            walletState = walletState,
            onConnectClick = onConnectWallet,
            modifier = Modifier.padding(16.dp)

            when {
                uiState.isLoading -> FullScreenLoading()
                uiState.error != null -> ErrorState(
                    error = uiState.error,
                    onRetry = onConnectWallet
                )
                uiState.pools.isEmpty() -> EmptyPoolState()
                else -> PoolList(
                    pools = uiState.pools,
                    onSnipeClick = onSnipePool
                )
            }
    }
}

@Composable
private fun PoolList(
    pools: List<Pool>,
    onSnipeClick: (Pool) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(pools, key = { it.id }) { pool ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                PoolCard(
                    pool = pool,
                    onSnipeClick = { onSnipeClick(pool) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                )
            }
        }
    }
}

@Composable
private fun EmptyPoolState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.WaterDrop,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_pools_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.no_pools_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp
            )
            Text(
                text = stringResource(R.string.loading_pools),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.pool_load_error),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error
        )
        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(stringResource(R.string.try_again))
        }
    }
}