package com.solanasniper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solanasniper.R
import com.solanasniper.ui.components.DexTypeSelector
import com.solanasniper.ui.components.FormInputField
import com.solanasniper.ui.viewmodels.SniperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SniperScreen(
    poolAddress: String? = null,
    onBackClick: () -> Unit,
    viewModel: SniperViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(poolAddress) {
        poolAddress?.let { viewModel.initializeWithPool(it) }
    }

    Scaffold(
        topBar = {
            SniperScreenTopBar(onBackClick = onBackClick)
        }
    ) { padding ->
        SniperContent(
            modifier = Modifier.padding(padding),
            uiState = uiState,
            viewModel = viewModel,
            focusManager = focusManager,
            context = context
        )
    }
}

@Composable
private fun SniperContent(
    modifier: Modifier = Modifier,
    uiState: SniperState,
    viewModel: SniperViewModel,
    focusManager: FocusManager,
    context: Context
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DexTypeSection(
            selectedDex = uiState.dexType,
            onDexSelected = viewModel::updateDexType
        )

        SnipeForm(
            uiState = uiState,
            onTokenAddressChange = viewModel::updateTokenAddress,
            onAmountChange = viewModel::updateAmount,
            onSlippageChange = viewModel::updateSlippage,
            focusManager = focusManager
        )

        ActionSection(
            isEnabled = uiState.isFormValid,
            isLoading = uiState.isProcessing,
            onSnipeClick = {
                focusManager.clearFocus()
                viewModel.executeSnipe()
            }
        )

        uiState.error?.let { error ->
            ErrorMessage(
                message = error.asString(context),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DexTypeSection(
    selectedDex: DexType,
    onDexSelected: (DexType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.dex_platform),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DexTypeSelector(
            selected = selectedDex,
            onSelect = onDexSelected
        )
    }
}

@Composable
private fun SnipeForm(
    uiState: SniperState,
    onTokenAddressChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSlippageChange: (String) -> Unit,
    focusManager: FocusManager
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormInputField(
            label = stringResource(R.string.token_address),
            value = uiState.tokenAddress,
            onValueChange = onTokenAddressChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            visualTransformation = AddressTransformation(),
            trailingIcon = {
                if (uiState.isAddressValid) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.valid_address),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        FormInputField(
            label = stringResource(R.string.amount_sol),
            value = uiState.amount,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            prefix = { Text("◎") },
            suffix = { Text("SOL") }
        )

        FormInputField(
            label = stringResource(R.string.slippage_percent),
            value = uiState.slippage.toString(),
            onValueChange = onSlippageChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            suffix = { Text("%") },
            onImeActionPerformed = { focusManager.clearFocus() }
        )
    }
}

@Composable
private fun ActionSection(
    isEnabled: Boolean,
    isLoading: Boolean,
    onSnipeClick: () -> Unit
) {
    Button(
        onClick = onSnipeClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = isEnabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = stringResource(R.string.start_snipe),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SniperScreenTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.snipe_configuration)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

// Address visual transformation
private class AddressTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 10) {
            text.text.take(6) + "..." + text.text.takeLast(4)
        } else text.text

        return TransformedText(
            AnnotatedString(trimmed),
            OffsetMapping.Identity
        )
    }
}}