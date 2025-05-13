package com.solanasniper.data.dex.raydium

import com.solanasniper.data.api.RaydiumApi
import com.solanasniper.data.model.RaydiumSwapRequest
import com.solanasniper.data.model.RaydiumSwapResponse
import com.solanasniper.domain.dex.DexSwapper
import com.solanasniper.domain.model.DexType
import com.solanasniper.domain.model.SwapParams
import com.solanasniper.domain.model.SwapResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RaydiumSwapper @Inject constructor(
    private val api: RaydiumApi
) : DexSwapper {

    override fun swap(params: SwapParams): Flow<SwapResult> = flow {
        try {
            emit(SwapResult.Loading)

            val request = RaydiumSwapRequest(
                inputMint = params.inputMint,
                outputMint = params.outputMint,
                amount = params.amountLamports,
                slippage = params.slippage,
                walletAddress = params.userAddress
            )

            val response: RaydiumSwapResponse = api.createSwap(request)

            emit(SwapResult.Success(
                transaction = response.encodedTx,
                feeLamports = response.feeLamports
            ))

        } catch (e: Exception) {
            emit(SwapResult.Error("Raydium swap failed: ${e.message}"))
        }
    }

    override fun supportsDex() = DexType.RAYDIUM

    override fun validateSwapParams(params: SwapParams): Boolean {
        return super.validateSwapParams(params) &&
                params.userAddress.isNotBlank()
    }
}