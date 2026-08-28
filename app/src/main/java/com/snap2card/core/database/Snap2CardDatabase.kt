package com.snap2card.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.snap2card.feature.deck.data.local.dao.CardDao
import com.snap2card.feature.deck.data.local.dao.DeckDao
import com.snap2card.feature.deck.data.local.entity.CardEntity
import com.snap2card.feature.deck.data.local.entity.DeckEntity
import com.snap2card.feature.study.data.local.dao.ReviewRecordDao
import com.snap2card.feature.study.data.local.entity.ReviewRecordEntity

/**
 * Root Room database.
 * Add new entities to the entities array and bump the version when schema changes.
 * Owner: shared — coordinate with the lead before modifying.
 */
@Database(
    entities = [
        DeckEntity::class,
        CardEntity::class,
        ReviewRecordEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class Snap2CardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun reviewRecordDao(): ReviewRecordDao
}
