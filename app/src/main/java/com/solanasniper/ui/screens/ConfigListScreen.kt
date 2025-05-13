package com.solanasniper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solanasniper.R
import com.solanasniper.ui.components.ConfigItem
import com.solanasniper.ui.components.EmptyState
import com.solanasniper.ui.components.LoadingState
import com.solanasniper.ui.viewmodels.ConfigListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigListScreen(
    onAddClick: () -> Unit,
    onEditClick: (configId: String) -> Unit,
    viewModel: ConfigListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_configs)) },
                actions = {
                    IconButton(
                        onClick = { viewModel.refresh() },
                        enabled = !uiState.isRefreshing
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, stringResource(R.string.refresh))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text(stringResource(R.string.new_config)) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            when {
                uiState.isLoading -> FullScreenLoading()
                uiState.error != null -> ErrorState(
                    error = uiState.error,
                    onRetry = viewModel::refresh
                )
                uiState.configs.isEmpty() -> EmptyConfigList(
                    onAddClick = onAddClick
                )
                else -> ConfigsList(
                    configs = uiState.configs,
                    onEditClick = onEditClick,
                    onDeleteClick = { showDeleteDialog = it }
                )
            }

            // Диалог подтверждения удаления
            showDeleteDialog?.let { configId ->
                DeleteConfirmationDialog(
                    onConfirm = {
                        viewModel.deleteConfig(configId)
                        showDeleteDialog = null
                    },
                    onDismiss = { showDeleteDialog = null }
                )
            }
        }
    }
}

@Composable
private fun ConfigsList(
    configs: List<ConfigUiItem>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(false),
        onRefresh = { viewModel.refresh() }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(configs, key = { it.id }) { config ->
                SwipeToDismiss(
                    modifier = Modifier.animateItemPlacement(),
                    background = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    },
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThreshold = { 0.5f },
                    state = rememberDismissState {
                        if (it == DismissValue.DismissedToEnd) {
                            onDeleteClick(config.id)
                        }
                        true
                    }
                ) {
                    ConfigItem(
                        config = config,
                        onEditClick = { onEditClick(config.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditClick(config.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyConfigList(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Tune,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_configs_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_configs_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAddClick) {
            Text(stringResource(R.string.create_first_config))
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_config_title)) },
        text = { Text(stringResource(R.string.delete_config_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_loading_configs),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.try_again))
        }
    }
}}