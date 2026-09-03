package com.snap2card.di

import com.snap2card.feature.account.data.remote.AccountApiService
import com.snap2card.feature.auth.data.remote.AuthApiService
import com.snap2card.feature.deck.data.remote.DeckApiService
import com.snap2card.feature.snap2card.data.remote.CardApiService
import com.snap2card.feature.snap2card.data.vocabulary.remote.VocabularyApiService
import com.snap2card.feature.study.data.remote.dto.ExamApiService
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
    fun provideCardApiService(retrofit: Retrofit): CardApiService =
        retrofit.create(CardApiService::class.java)

    @Provides @Singleton
    fun provideAccountApiService(retrofit: Retrofit): AccountApiService =
        retrofit.create(AccountApiService::class.java)

    @Provides @Singleton
    fun provideVocabularyApiService(retrofit: Retrofit): VocabularyApiService =
        retrofit.create(VocabularyApiService::class.java)

    @Provides @Singleton
    fun provideExamApiService(retrofit: Retrofit): ExamApiService =
        retrofit.create(ExamApiService::class.java)
}
