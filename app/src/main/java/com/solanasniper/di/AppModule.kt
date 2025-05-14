package com.solanasniper.di

import android.content.Context
import androidx.room.Room
import com.solanasniper.data.api.*
import com.solanasniper.data.database.AppDatabase
import com.solanasniper.data.dao.ConfigDao
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
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.raydium.io/") // Замените на нужный baseUrl
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRaydiumApi(retrofit: Retrofit): RaydiumApi =
        retrofit.create(RaydiumApi::class.java)

    @Provides
    @Singleton
    fun provideJupiterApi(retrofit: Retrofit): JupiterApi =
        retrofit.create(JupiterApi::class.java)

    @Provides
    @Singleton
    fun provideBirdeyeApi(retrofit: Retrofit): BirdeyeApi =
        retrofit.create(BirdeyeApi::class.java)

    @Provides
    @Singleton
    fun provideJitoApi(retrofit: Retrofit): JitoApi =
        retrofit.create(JitoApi::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "sniper-db").build()

    @Provides
    fun provideConfigDao(appDatabase: AppDatabase): ConfigDao =
        appDatabase.configDao()

    @Provides
    @Singleton
    fun provideConfigRepository(configDao: ConfigDao): ConfigRepository =
        ConfigRepositoryImpl(configDao)

    @Provides
    @Singleton
    fun providePoolRepository(
        api: BirdeyeApi,
        database: AppDatabase
    ): PoolRepository = PoolRepository(api, database)

    @Provides
    @Singleton
    fun provideWalletRepository(): WalletRepository = WalletRepository()

    @Provides
    @Singleton
    fun provideSwapRepository(
        raydiumApi: RaydiumApi,
        jupiterApi: JupiterApi
    ): SwapRepository = SwapRepository(raydiumApi, jupiterApi)

    @Provides
    @Singleton
    fun provideSolanaUtils(): SolanaUtils = SolanaUtils()
}