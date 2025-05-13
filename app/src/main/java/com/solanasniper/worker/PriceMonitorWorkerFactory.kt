package com.solanasniper.worker

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.solanasniper.data.repository.ConfigRepository
import com.solanasniper.data.repository.PoolRepository
import com.solanasniper.utils.NotificationHelper
import javax.inject.Inject

class PriceMonitorWorkerFactory @Inject constructor(
    private val configRepo: ConfigRepository,
    private val poolRepo: PoolRepository,
    private val notifier: NotificationHelper
) : HiltWorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            PriceMonitorWorker::class.java.name ->
                PriceMonitorWorker(appContext, workerParameters, configRepo, poolRepo, notifier)
            else -> null
        }
    }
}