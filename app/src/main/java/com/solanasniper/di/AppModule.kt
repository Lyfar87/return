package com.solanasniper.di

import android.content.Context
import com.solanasniper.data.api.*
import com.solanasniper.data.repository.*
import com.solanasniper.utils.SolanaUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBirdeyeApi(): BirdeyeApi = Retrofit.Builder()
        .baseUrl(BirdeyeApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BirdeyeApi::class.java)

    @Provides
    @Singleton
    fun provideJitoApi(): JitoApi = Retrofit.Builder()
        .baseUrl(JitoApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JitoApi::class.java)

    @Provides
    @Singleton
    fun provideRaydiumApi(): RaydiumApi = Retrofit.Builder()
        .baseUrl(RaydiumApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RaydiumApi::class.java)

    @Provides
    @Singleton
    fun provideJupiterApi(): JupiterApi = Retrofit.Builder()
        .baseUrl(JupiterApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JupiterApi::class.java)

    @Provides
    @Singleton
    fun providePoolRepository(
        api: BirdeyeApi,
        db: AppDatabase
    ): PoolRepository = PoolRepository(api, db)

    @Provides
    @Singleton
    fun provideWalletRepository(
        utils: SolanaUtils
    ): WalletRepository = WalletRepository(utils)

    @Provides
    @Singleton
    fun provideSolanaUtils(@ApplicationContext context: Context): SolanaUtils = SolanaUtils()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
}