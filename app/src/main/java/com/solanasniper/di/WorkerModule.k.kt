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

    // Предоставляет WorkManager с контекстом приложения
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    // Предоставляет кастомную фабрику для воркеров
    @Provides
    @Singleton
    fun providePriceMonitorWorkerFactory(
        configRepo: ConfigRepository,
        poolRepo: PoolRepository,
        notifier: NotificationHelper
    ): PriceMonitorWorkerFactory {
        return PriceMonitorWorkerFactory(configRepo, poolRepo, notifier)
    }

    // Настраивает конфигурацию WorkManager с фабрикой
    @Provides
    @Singleton
    fun provideWorkerConfiguration(
        factory: PriceMonitorWorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(factory)
            .build()
    }

    // Интеграция с HiltWorkerFactory
    @Provides
    @Singleton
    fun provideHiltWorkerFactory(
        factory: PriceMonitorWorkerFactory
    ): HiltWorkerFactory {
        return factory
    }
}