package com.snap2card.di

import com.snap2card.feature.snap2card.data.service.MlKitTextRecognitionService
import com.snap2card.feature.snap2card.domain.service.TextRecognitionService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TextRecognitionModule {
    @Binds
    @Singleton
    abstract fun bindTextRecognitionService(
        impl: MlKitTextRecognitionService,
    ): TextRecognitionService
}
