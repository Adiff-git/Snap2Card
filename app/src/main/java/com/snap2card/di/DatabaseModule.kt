package com.snap2card.di

import android.content.Context
import androidx.room.Room
import com.snap2card.core.database.Snap2CardDatabase
import com.snap2card.feature.deck.data.local.dao.CardDao
import com.snap2card.feature.deck.data.local.dao.DeckDao
import com.snap2card.feature.study.data.local.dao.ReviewRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Snap2CardDatabase =
        Room.databaseBuilder(context, Snap2CardDatabase::class.java, "snap2card.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideDeckDao(db: Snap2CardDatabase): DeckDao = db.deckDao()
    @Provides fun provideCardDao(db: Snap2CardDatabase): CardDao = db.cardDao()
    @Provides fun provideReviewRecordDao(db: Snap2CardDatabase): ReviewRecordDao = db.reviewRecordDao()
}
