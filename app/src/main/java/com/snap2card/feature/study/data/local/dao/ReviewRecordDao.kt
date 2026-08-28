package com.snap2card.feature.study.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snap2card.feature.study.data.local.entity.ReviewRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(record: ReviewRecordEntity)

    @Query("SELECT * FROM review_records WHERE deckId = :deckId ORDER BY reviewedAt DESC")
    fun getReviewsForDeck(deckId: String): Flow<List<ReviewRecordEntity>>

    @Query("SELECT * FROM review_records ORDER BY reviewedAt DESC")
    fun getAllReviews(): Flow<List<ReviewRecordEntity>>
}
