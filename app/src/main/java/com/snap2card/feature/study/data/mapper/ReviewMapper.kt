package com.snap2card.feature.study.data.mapper

import com.snap2card.core.util.DateUtil.toEpochMillis
import com.snap2card.feature.study.data.local.entity.ReviewRecordEntity
import com.snap2card.feature.study.data.remote.dto.ExamReviewLogDetailData
import com.snap2card.feature.study.domain.model.ExamReviewDetail
import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult

fun ReviewRecordEntity.toDomain() = ReviewRecord(
    id = id,
    cardId = cardId,
    deckId = deckId,
    result = ReviewResult.valueOf(result),
    reviewedAt = reviewedAt,
)

fun ReviewRecord.toEntity() = ReviewRecordEntity(
    id = id,
    cardId = cardId,
    deckId = deckId,
    result = result.name,
    reviewedAt = reviewedAt,
)

fun ExamReviewLogDetailData.toDomain() = ExamReviewDetail(
    logId = logId,
    examName = examName,
    examLevel = examLevel,
    resultScore = resultScore,
    totalScore = totalScore,
    numOfQuiz = numOfQuiz,
    completedAt = dateDone.toEpochMillis(),
    quizResults = quizResults.map { it.toDomain() },
)
