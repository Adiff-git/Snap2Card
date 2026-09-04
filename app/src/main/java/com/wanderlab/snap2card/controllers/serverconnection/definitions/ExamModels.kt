package com.wanderlab.snap2card.controllers.serverconnection.definitions

data class CreateExamRequest(
    val categoryId: String
)

data class CreateExamResponse(
    val examId: String
)

data class StartExamRequest(
    val examId: String
)

data class StartExamResponse(
    val examLogId: String
)

data class ExamResultRequest(
    val examLogId: String,
    val quizId: String,
    val result: Boolean
)

data class ExamReviewRequest(
    val examId: String
)

data class QuizInfo(
    val quizId: String,
    val frontSide: String,
    val backSide: String
)

data class ExamReviewResponse(
    val numOfQuiz: Int,
    val quizzes: List<QuizInfo>
)

data class ExamCompletedRequest(
    val examLogId: String
)

data class ExamInfo(
    val examId: String,
    val examName: String,
    val numOfQuestion: Int,
    val dateCreated: Time
)