package com.snap2card.feature.study.data.remote.dto

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