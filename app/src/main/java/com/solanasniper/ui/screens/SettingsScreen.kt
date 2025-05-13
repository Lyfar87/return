package com.solanasniper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solanasniper.R
import com.solanasniper.ui.components.RpcSelector
import com.solanasniper.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SettingsContent(
            modifier = Modifier.padding(padding),
            uiState = uiState,
            viewModel = viewModel,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        SettingsSection(title = stringResource(R.string.network_settings)) {
            RpcSelector(
                currentRpc = uiState.currentRpc,
                availableRpcs = uiState.availableRpcs,
                onRpcSelected = { viewModel.selectRpc(it) },
                onCustomRpcAdded = { name, url ->
                    if (NetworkUtils.isValidUrl(url)) {
                        viewModel.addCustomRpc(name, url)
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.invalid_rpc_url),
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )
        }

        SettingsSection(title = stringResource(R.string.mev_settings)) {
            SwitchPreference(
                label = stringResource(R.string.enable_jito_bundles),
                checked = uiState.useJitoBundles,
                onCheckedChange = { viewModel.setJitoBundlesEnabled(it) }
            )
            SliderPreference(
                label = stringResource(R.string.fee_multiplier),
                value = uiState.feeMultiplier.toFloat(),
                onValueChange = { viewModel.setFeeMultiplier(it.toDouble()) },
                valueRange = 1f..3f,
                steps = 4,
                valueFormatter = { "%.1fx".format(it) }
            )
        }

        SettingsSection(title = stringResource(R.string.general_settings)) {
            SwitchPreference(
                label = stringResource(R.string.price_alerts),
                checked = uiState.priceAlertsEnabled,
                onCheckedChange = { viewModel.setPriceAlertsEnabled(it) }
            )
            SwitchPreference(
                label = stringResource(R.string.dark_theme),
                checked = uiState.darkThemeEnabled,
                onCheckedChange = { viewModel.setDarkThemeEnabled(it) }
            )
        }

        SettingsSection(title = stringResource(R.string.advanced_settings)) {
            SwitchPreference(
                label = stringResource(R.string.debug_mode),
                checked = uiState.debugModeEnabled,
                onCheckedChange = { viewModel.setDebugModeEnabled(it) }
            )
            Button(
                onClick = { viewModel.resetToDefaults() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.reset_defaults))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun SwitchPreference(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                role = Role.Switch
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.semantics {
                this.contentDescription = "$label Switch"
            }
        )
    }
}

@Composable
private fun SliderPreference(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueFormatter: (Float) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "$label: ${valueFormatter(value)}",
            style = MaterialTheme.typography.bodyLarge
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    )
}}