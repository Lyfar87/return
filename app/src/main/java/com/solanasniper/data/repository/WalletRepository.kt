package com.solanasniper.data.repository

import com.solanasniper.data.model.WalletState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor() {
    private val _walletState = MutableStateFlow<WalletState>(WalletState.Disconnected)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    suspend fun connectWallet() {
        // Заглушка для реализации подключения
        _walletState.value = WalletState.Connecting
        // Здесь должна быть реальная логика подключения
        _walletState.value = WalletState.Connected(
            address = "FakeAddress123...",
            balance = 0.0
        )
    }

    fun disconnect() {
        _walletState.value = WalletState.Disconnected
    }

    fun getCurrentAddress(): String? {
        return (_walletState.value as? WalletState.Connected)?.address
    }
}