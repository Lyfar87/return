package com.solanasniper.domain.dex

import com.solanasniper.domain.model.SwapParams
import com.solanasniper.domain.model.SwapResult
import kotlinx.coroutines.flow.Flow

interface DexSwapper {
    fun swap(params: SwapParams): Flow<SwapResult>

    fun supportsDex(): DexType

    fun validateSwapParams(params: SwapParams): Boolean {
        return params.amountLamports > 0 &&
                params.slippage in 0.1..50.0 &&
                params.inputMint.isNotBlank() &&
                params.outputMint.isNotBlank()
    }

    companion object {
        const val MAX_RETRIES = 3
        const val DEFAULT_SLIPPAGE = 1.0
    }
}