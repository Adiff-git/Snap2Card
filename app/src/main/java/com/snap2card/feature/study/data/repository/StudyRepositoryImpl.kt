package com.snap2card.feature.study.data.repository

import com.snap2card.core.util.DateUtil
import com.snap2card.feature.study.data.local.dao.ReviewRecordDao
import com.snap2card.feature.study.data.local.entity.ReviewRecordEntity
import com.snap2card.feature.study.data.mapper.toDomain
import com.snap2card.feature.study.data.remote.dto.CompleteExamRequest
import com.snap2card.feature.study.data.remote.dto.CreateExamRequest
import com.snap2card.feature.study.data.remote.dto.ExamApiService
import com.snap2card.feature.study.data.remote.dto.ExamResultRequest
import com.snap2card.feature.study.data.remote.dto.StartExamRequest
import com.snap2card.feature.study.domain.model.ExamSession
import com.snap2card.feature.study.domain.model.ReviewQuiz
import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult
import com.snap2card.feature.study.domain.model.ExamReviewDetail
import com.snap2card.feature.study.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val reviewRecordDao: ReviewRecordDao,
    private val api: ExamApiService,
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

    override suspend fun createExam(categoryId: String): Result<String> = runCatching {
        api.createExam(CreateExamRequest(categoryId)).data?.examId
            ?: error("Missing examId in response")
    }

    override suspend fun startExam(examId: String): Result<ExamSession> = runCatching {
        val logId = api.startExam(StartExamRequest(examId)).data?.examLogId
            ?: error("Missing examLogId in response")
        ExamSession(examId, logId)
    }

    override suspend fun getExamReview(examId: String): Result<List<ReviewQuiz>> = runCatching {
        api.reviewExam(examId).data?.quizzes
            ?.map { ReviewQuiz(it.quizId, it.frontSide, it.backSide) }
            ?: emptyList()
    }

    override suspend fun submitResult(examLogId: String, quizId: String, result: Boolean): Result<Unit> =
        runCatching { api.submitResult(ExamResultRequest(examLogId, quizId, result)) }
            .map { }

    override suspend fun completeExam(examLogId: String): Result<Unit> =
        runCatching { api.completeExam(CompleteExamRequest(examLogId)) }.map { }

    override suspend fun getExamReviewDetail(examLogId: String): Result<ExamReviewDetail> = runCatching {
        api.getReviewLogDetail(examLogId).data?.toDomain() ?: error("Missing review detail in response")
    }
}
