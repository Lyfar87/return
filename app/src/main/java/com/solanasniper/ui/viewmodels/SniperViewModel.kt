package com.solanasniper.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solanasniper.data.dex.DexSwapperFactory
import com.solanasniper.data.mev.MevProtector
import com.solanasniper.data.repository.ConfigRepository
import com.solanasniper.domain.model.DexType
import com.solanasniper.domain.model.SwapParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SniperViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val dexSwapperFactory: DexSwapperFactory,
    private val mevProtector: MevProtector
) : ViewModel() {

    private val _uiState = MutableStateFlow(SniperState())
    val uiState = _uiState.asStateFlow()

    fun initializeWithPool(poolAddress: String) {
        viewModelScope.launch {
            // Загрузка данных пула и инициализация полей
        }
    }

    fun updateDexType(dexType: DexType) {
        _uiState.update { it.copy(dexType = dexType) }
    }

    fun updateTokenAddress(address: String) {
        _uiState.update {
            it.copy(
                tokenAddress = address,
                isAddressValid = validateAddress(address)
            )
        }
    }

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(
                amount = amount,
                isAmountValid = amount.toDoubleOrNull()?.let { it > 0 } ?: false
            )
        }
    }

    fun updateSlippage(slippage: String) {
        _uiState.update {
            it.copy(
                slippage = slippage.toDoubleOrNull() ?: 1.0,
                isSlippageValid = slippage.toDoubleOrNull()?.let { it in 0.1..50.0 } ?: false
            )
        }
    }

    fun executeSnipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            try {
                val params = SwapParams(
                    inputMint = "SOL", // Заменить на реальные данные
                    outputMint = _uiState.value.tokenAddress,
                    amountLamports = (_uiState.value.amount.toDouble() * 1e9).toLong(),
                    slippage = _uiState.value.slippage,
                    userAddress = "WALLET_ADDRESS" // Получить из кошелька
                )

                val swapper = dexSwapperFactory.create(_uiState.value.dexType)
                val result = swapper.swap(params)

                mevProtector.protectTransaction(result.transaction).let {
                    // Отправка транзакции в сеть
                }

                configRepository.saveConfig(_uiState.value.toConfigEntity())
                _uiState.update { it.copy(isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun validateAddress(address: String): Boolean {
        return address.matches("^[1-9A-HJ-NP-Za-km-z]{32,44}$".toRegex())
    }

    private fun SniperState.toConfigEntity() =
        ConfigEntity(
            tokenAddress = tokenAddress,
            dexType = dexType.name,
            amount = amount.toDouble(),
            slippage = slippage
        )
}

data class SniperState(
    val dexType: DexType = DexType.RAYDIUM,
    val tokenAddress: String = "",
    val amount: String = "",
    val slippage: Double = 1.0,
    val isAddressValid: Boolean = false,
    val isAmountValid: Boolean = false,
    val isSlippageValid: Boolean = true,
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean
        get() = isAddressValid && isAmountValid && isSlippageValid
}