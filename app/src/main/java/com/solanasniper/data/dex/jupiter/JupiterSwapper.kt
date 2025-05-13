package com.solanasniper.data.dex.jupiter

import com.solanasniper.data.api.JupiterApi
import com.solanasniper.domain.dex.DexSwapper
import com.solanasniper.domain.model.DexType
import com.solanasniper.domain.model.SwapParams
import com.solanasniper.domain.model.SwapResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class JupiterSwapper @Inject constructor(
    private val api: JupiterApi
) : DexSwapper {

    override fun swap(params: SwapParams): Flow<SwapResult> = flow {
        try {
            emit(SwapResult.Loading)

            // 1. Получение котировки
            val quote = api.getQuote(
                inputMint = params.inputMint,
                outputMint = params.outputMint,
                amount = params.amountLamports,
                slippageBps = (params.slippage * 100).toInt()
            )

            // 2. Создание транзакции
            val swapTx = api.createSwapTransaction(
                quote = quote,
                userPublicKey = params.userAddress
            )

            emit(SwapResult.Success(
                transaction = swapTx.swapTransaction,
                feeLamports = swapTx.prioritizationFeeLamports
            ))

        } catch (e: Exception) {
            emit(SwapResult.Error("Jupiter swap failed: ${e.localizedMessage}"))
        }
    }

    override fun supportsDex() = DexType.JUPITER

    override fun validateSwapParams(params: SwapParams): Boolean {
        return super.validateSwapParams(params) &&
                params.userAddress.matches("^[1-9A-HJ-NP-Za-km-z]{32,44}$".toRegex())
    }
}