package com.snap2card.feature.study.data.remote.dto

import com.snap2card.feature.deck.data.remote.dto.ApiTimeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateExamRequest(val categoryId: String)

@Serializable
data class CreateExamData(val examId: String)

@Serializable
data class StartExamRequest(val examId: String)

// ASSUMPTION: verify with backend — adding nullable examLogId since the doc doesn't show one
@Serializable
data class StartExamData(val examLogId: String? = null)
@Serializable
data class ReviewExamData(val numOfQuiz: Int, val quizzes: List<QuizDto>)

@Serializable
data class QuizDto(val quizId: String, val frontSide: String, val backSide: String)

@Serializable
data class ExamResultRequest(val examLogId: String, val quizId: String, val result: Boolean)

@Serializable
data class CompleteExamRequest(val examLogId: String)

@Serializable
data class ApiResponse<T>(val status: String, val data: T? = null)

@Serializable
data class StatusOnlyResponse(val status: String)

@Serializable
data class ExamReviewLogDetailData(
    @SerialName("logId") val logId: String,
    @SerialName("examName") val examName: String,
    @SerialName("examLevel") val examLevel: String,
    @SerialName("resultScore") val resultScore: Int? = null,
    @SerialName("totalScore") val totalScore: Int,
    @SerialName("numOfQuiz") val numOfQuiz: Int,
    @SerialName("dateDone") val dateDone: ApiTimeDto,
    @SerialName("quizResults") val quizResults: List<QuizResultDto>,
)

@Serializable
data class QuizResultDto(
    @SerialName("quizId") val quizId: String,
    @SerialName("frontSide") val frontSide: String,
    @SerialName("backSide") val backSide: String,
    @SerialName("accountAnswer") val accountAnswer: Boolean,
    @SerialName("resultScore") val resultScore: Int,
    @SerialName("totalScore") val totalScore: Int,
)