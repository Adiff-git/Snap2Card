package com.snap2card.feature.study.data.repository

import com.snap2card.core.util.DateUtil
import com.snap2card.feature.study.data.local.dao.ReviewRecordDao
import com.snap2card.feature.study.data.local.entity.ReviewRecordEntity
import com.snap2card.feature.study.data.mapper.toDomain
import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult
import com.snap2card.feature.study.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val reviewRecordDao: ReviewRecordDao,
) : StudyRepository {

    override suspend fun recordReview(cardId: String, deckId: String, result: ReviewResult) {
        reviewRecordDao.insertReview(
            ReviewRecordEntity(
                id = UUID.randomUUID().toString(),
                cardId = cardId,
                deckId = deckId,
                result = result.name,
                reviewedAt = DateUtil.now(),
            )
        )
    }

    override fun getReviewsForDeck(deckId: String): Flow<List<ReviewRecord>> =
        reviewRecordDao.getReviewsForDeck(deckId).map { it.map { e -> e.toDomain() } }

    override fun getAllReviews(): Flow<List<ReviewRecord>> =
        reviewRecordDao.getAllReviews().map { it.map { e -> e.toDomain() } }
}
