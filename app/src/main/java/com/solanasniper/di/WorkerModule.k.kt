package com.solanasniper.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.solanasniper.data.repository.ConfigRepository
import com.solanasniper.data.repository.PoolRepository
import com.solanasniper.utils.NotificationHelper
import com.solanasniper.worker.PriceMonitorWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun providePriceMonitorWorkerFactory(
        configRepository: ConfigRepository,
        poolRepository: PoolRepository,
        notificationHelper: NotificationHelper
    ): PriceMonitorWorkerFactory =
        PriceMonitorWorkerFactory(configRepository, poolRepository, notificationHelper)

    @Provides
    @Singleton
    fun provideWorkerConfiguration(
        workerFactory: PriceMonitorWorkerFactory
    ): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    // Если вы используете HiltWorkerFactory для Hilt-воркеров, раскомментируйте этот метод:
    /*
    @Provides
    @Singleton
    fun provideHiltWorkerFactory(
        hiltWorkerFactory: HiltWorkerFactory
    ): HiltWorkerFactory = hiltWorkerFactory
    */
}