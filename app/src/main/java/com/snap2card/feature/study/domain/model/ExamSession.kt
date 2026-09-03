package com.snap2card.feature.study.domain.model

data class ExamSession(val examId: String, val examLogId: String)

data class ReviewQuiz(val quizId: String, val front: String, val back: String)