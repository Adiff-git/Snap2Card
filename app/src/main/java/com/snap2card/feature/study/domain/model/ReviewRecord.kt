package com.snap2card.feature.study.domain.model

/** Records the result of reviewing a single card during a study session. */
data class ReviewRecord(
    val id: String,
    val cardId: String,
    val deckId: String,
    val result: ReviewResult,
    val reviewedAt: Long,
)

enum class ReviewResult {
    GOT_IT,
    AGAIN,
}

data class ExamReviewDetail(
    val logId: String,
    val examName: String,
    val examLevel: String,
    val resultScore: Int?,
    val totalScore: Int,
    val numOfQuiz: Int,
    val completedAt: Long?,
    val quizResults: List<QuizResult>,
)

data class QuizResult(
    val quizId: String,
    val frontSide: String,
    val backSide: String,
    val wasAttempted: Boolean,
    val isCorrect: Boolean,
)
