package com.solanasniper.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.solanasniper.data.repository.ConfigRepository
import com.solanasniper.data.repository.PoolRepository
import com.solanasniper.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PriceMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val configRepository: ConfigRepository,
    private val poolRepository: PoolRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Получить активные конфигурации
            val configs = configRepository.getActiveConfigs()

            // 2. Проверить условия для каждой конфигурации
            configs.forEach { config ->
                val pool = poolRepository.getPoolByAddress(config.tokenAddress)
                pool?.let {
                    when (config.checkTriggers(pool.currentPrice)) {
                        SnipeConfig.TriggerResult.STOP_LOSS ->
                            notificationHelper.showSnipeAlert(
                                "Stop Loss Triggered",
                                "${pool.pairName} reached stop loss price"
                            )

                        SnipeConfig.TriggerResult.TAKE_PROFIT ->
                            notificationHelper.showSnipeAlert(
                                "Take Profit Triggered",
                                "${pool.pairName} reached target price"
                            )

                        else -> Unit
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun setupPeriodicWork() = PeriodicWorkRequestBuilder<PriceMonitorWorker>(
            15, // Повтор каждые 15 минут
            TimeUnit.MINUTES
        ).build()
    }
}