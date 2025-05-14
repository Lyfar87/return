package com.solanasniper.data.model

sealed class WalletState {
    object Disconnected : WalletState()
    object Connecting : WalletState()
    data class Connected(val address: String, val balance: Double) : WalletState()
    data class Error(val message: String) : WalletState()
}