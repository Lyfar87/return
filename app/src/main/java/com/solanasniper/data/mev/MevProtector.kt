package com.solanasniper.data.mev

import com.solanasniper.data.api.JitoApi
import com.solanasniper.data.model.Transaction
import kotlinx.coroutines.delay
import java.util.Random
import javax.inject.Inject

class MevProtector @Inject constructor(
    private val jitoApi: JitoApi
) {
    private val random = Random()
    private var feeMultiplier = 1.5 // Умножитель комиссий по умолчанию

    /**
     * Применяет комплекс MEV-защитных мер к транзакции
     * @param tx Исходная транзакция
     * @return Защищенная транзакция
     */
    suspend fun protect(tx: Transaction): Transaction {
        // 1. Рандомная задержка (100-500 мс)
        applyRandomDelay()

        // 2. Повышение комиссии
        val boostedTx = applyPriorityFee(tx)

        // 3. Отправка через Jito Bundle
        return sendViaJito(boostedTx)
    }

    /**
     * Добавляет случайную задержку для избежания фронтраннинга
     */
    private suspend fun applyRandomDelay(min: Int = 100, max: Int = 500) {
        delay(random.nextInt(max - min) + min.toLong())
    }

    /**
     * Увеличивает комиссию транзакции
     */
    private fun applyPriorityFee(tx: Transaction): Transaction {
        return tx.copy(
            fee = (tx.fee * feeMultiplier).toLong()
        )
    }

    /**
     * Отправляет транзакцию через Jito Bundles
     */
    private suspend fun sendViaJito(tx: Transaction): Transaction {
        val response = jitoApi.sendBundle(
            auth = "Bearer ${BuildConfig.JITO_API_KEY}",
            bundle = createJitoBundle(tx)
        )

        return if (response.isSuccessful) {
            tx.copy(status = Transaction.Status.CONFIRMED)
        } else {
            tx.copy(
                status = Transaction.Status.FAILED,
                error = response.errorBody()?.string()
            )
        }
    }

    /**
     * Создает Jito Bundle из транзакции
     */
    private fun createJitoBundle(tx: Transaction): JitoBundleRequest {
        return JitoBundleRequest(
            transactions = listOf(tx.rawData),
            fee = tx.fee,
            blockhash = null // Актуальный blockhash должен добавляться динамически
        )
    }

    fun updateFeeMultiplier(newMultiplier: Double) {
        require(newMultiplier >= 1.0) { "Invalid fee multiplier" }
        feeMultiplier = newMultiplier
    }
}