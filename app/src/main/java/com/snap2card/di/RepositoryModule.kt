package com.snap2card.di

import com.snap2card.feature.account.data.repository.AccountRepositoryImpl
import com.snap2card.feature.account.data.repository.FakeAuthRepositoryImpl
import com.snap2card.feature.account.domain.repository.AccountRepository
import com.snap2card.feature.auth.data.repository.AuthRepositoryImpl
import com.snap2card.feature.auth.domain.repository.AuthRepository
import com.snap2card.feature.deck.data.repository.DeckRepositoryImpl
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.history.data.repository.HistoryRepositoryImpl
import com.snap2card.feature.history.domain.repository.HistoryRepository
import com.snap2card.feature.home.data.repository.FakeDashboardRepository
import com.snap2card.feature.home.domain.repository.DashboardRepository
import com.snap2card.feature.settings.data.repository.SettingsRepositoryImpl
import com.snap2card.feature.settings.domain.repository.SettingsRepository
import com.snap2card.feature.snap2card.data.repository.OcrRepositoryImpl
import com.snap2card.feature.snap2card.data.vocabulary.repository.VocabularyGenerationRepositoryImpl
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import com.snap2card.feature.snap2card.domain.vocabulary.repository.VocabularyGenerationRepository
import com.snap2card.feature.study.data.repository.StudyRepositoryImpl
import com.snap2card.feature.study.domain.repository.StudyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces (domain) to their implementations (data).
 * Add new bindings here whenever a new repository is created.
 * Owner: shared — coordinate with the lead before modifying.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindDeckRepository(impl: DeckRepositoryImpl): DeckRepository

    @Binds @Singleton
    abstract fun bindOcrRepository(impl: OcrRepositoryImpl): OcrRepository

    @Binds @Singleton
    abstract fun bindStudyRepository(impl: StudyRepositoryImpl): StudyRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds @Singleton
    abstract fun bindDashboardRepository(impl: FakeDashboardRepository): DashboardRepository

    @Binds @Singleton
    abstract fun bindVocabularyGenerationRepository(impl: VocabularyGenerationRepositoryImpl): VocabularyGenerationRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository
}
