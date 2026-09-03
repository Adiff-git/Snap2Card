package com.snap2card.feature.study.data.remote.dto

import retrofit2.http.Body
import retrofit2.http.POST

interface ExamApiService {

    @POST("exams/create")
    suspend fun createExam(@Body request: CreateExamRequest): ApiResponse<CreateExamData>

    @POST("exams/start")
    suspend fun startExam(@Body request: StartExamRequest): ApiResponse<StartExamData>

    // TODO: confirm with backend — GET + body will crash via OkHttp.
    // If backend confirms query param instead, change to:
    // @GET("exams/review") suspend fun reviewExam(@Query("examId") examId: String): ApiResponse<ReviewExamData>
    @POST("exams/review")
    suspend fun reviewExam(@Body request: ReviewExamRequest): ApiResponse<ReviewExamData>

    @POST("exams/result")
    suspend fun submitResult(@Body request: ExamResultRequest): StatusOnlyResponse

    @POST("exams/completed")
    suspend fun completeExam(@Body request: CompleteExamRequest): StatusOnlyResponse
}