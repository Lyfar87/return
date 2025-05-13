package com.solanasniper

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SniperApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }

    override fun onCreate() {
        super.onCreate()
        // Дополнительная инициализация (аналитика, логирование и т.д.)
    }
}