package com.solanasniper.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solanasniper.data.repository.PoolRepository
import com.solanasniper.data.repository.WalletRepository
import com.solanasniper.domain.model.Pool
import com.solanasniper.domain.model.WalletState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val poolRepository: PoolRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                refreshPools()
                checkWalletConnection()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshPools() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val pools = poolRepository.getNewPools()
                _uiState.update {
                    it.copy(
                        pools = pools,
                        error = null,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to load pools: ${e.message}",
                        isRefreshing = false
                    )
                }
            }
        }
    }

    fun connectWallet() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            try {
                val walletState = walletRepository.connectWallet()
                _uiState.update {
                    it.copy(
                        walletState = walletState,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Wallet connection failed: ${e.message}",
                        walletState = WalletState.Error(e.message ?: "Unknown error")
                    )
                }
            } finally {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun checkWalletConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(walletState = walletRepository.getWalletState()) }
        }
    }
}

data class MainUiState(
    val pools: List<Pool> = emptyList(),
    val walletState: WalletState = WalletState.Disconnected,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null
)