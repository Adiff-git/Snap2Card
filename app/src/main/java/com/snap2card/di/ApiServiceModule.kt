package com.snap2card.di

import com.snap2card.feature.auth.data.remote.AuthApiService
import com.snap2card.feature.deck.data.remote.DeckApiService
import com.snap2card.feature.snap2card.data.remote.OcrApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Provides Retrofit API service instances.
 * Keep this file focused: one @Provides per service interface.
 * Add new services here as backend endpoints are finalized.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides @Singleton
    fun provideDeckApiService(retrofit: Retrofit): DeckApiService =
        retrofit.create(DeckApiService::class.java)

    @Provides @Singleton
    fun provideOcrApiService(retrofit: Retrofit): OcrApiService =
        retrofit.create(OcrApiService::class.java)

    @Provides @Singleton
    fun provideAccountApiService(retrofit: Retrofit): com.snap2card.feature.account.data.remote.AccountApiService =
        retrofit.create(com.snap2card.feature.account.data.remote.AccountApiService::class.java)
}
