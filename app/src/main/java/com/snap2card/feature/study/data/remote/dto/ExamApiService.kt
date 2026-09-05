package com.snap2card.feature.study.data.remote.dto

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ExamApiService {

    @POST("exams/create")
    suspend fun createExam(@Body request: CreateExamRequest): ApiResponse<CreateExamData>

    @POST("exams/start")
    suspend fun startExam(@Body request: StartExamRequest): ApiResponse<StartExamData>

    @Headers("Content-Type: application/json")
    @GET("exams/review")
    suspend fun reviewExam(@Query("examId") examId: String): ApiResponse<ReviewExamData>

    @POST("exams/result")
    suspend fun submitResult(@Body request: ExamResultRequest): StatusOnlyResponse

    @POST("exams/completed")
    suspend fun completeExam(@Body request: CompleteExamRequest): StatusOnlyResponse
}