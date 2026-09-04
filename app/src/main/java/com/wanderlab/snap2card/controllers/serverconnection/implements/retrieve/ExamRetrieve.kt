package com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ExamInfo
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ExamReviewResponse
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import com.wanderlab.snap2card.controllers.serverconnection.implements.toQuizInfoList
import com.wanderlab.snap2card.controllers.serverconnection.implements.toTime

internal class ExamRetrieve(private val executor: ApiExecutor) {

    suspend fun reviewExam(examId: String): ApiResult<ExamReviewResponse> =
        executor.execute(
            Endpoints.EXAM_REVIEW,
            query = listOf("examId" to examId)
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            ExamReviewResponse(
                numOfQuiz = data.getInt("numOfQuiz"),
                quizzes = data.getJSONArray("quizzes").toQuizInfoList()
            )
        }

    suspend fun listExams(): ApiResult<List<ExamInfo>> =
        executor.execute(
            Endpoints.EXAM_LIST
        ) { json ->
            val data = json.optJSONArray("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            buildList {
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    add(
                        ExamInfo(
                            examId = item.getString("examId"),
                            examName = item.getString("examName"),
                            numOfQuestion = item.getInt("numOfQuestion"),
                            dateCreated = item.getJSONObject("dateCreated").toTime()
                        )
                    )
                }
            }
        }
}