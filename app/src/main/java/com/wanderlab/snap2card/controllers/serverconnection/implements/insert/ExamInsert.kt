package com.wanderlab.snap2card.controllers.serverconnection.implements.insert

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateExamResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.StartExamResponse
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor

internal class ExamInsert(private val executor: ApiExecutor) {

    suspend fun createExam(categoryId: String): ApiResult<CreateExamResponse> =
        executor.execute(
            Endpoints.EXAM_CREATE,
            body = executor.jsonBody {
                put("categoryId", categoryId)
            }
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            CreateExamResponse(examId = data.getString("examId"))
        }

    suspend fun startExam(examId: String): ApiResult<StartExamResponse> =
        executor.execute(
            Endpoints.EXAM_START,
            body = executor.jsonBody {
                put("examId", examId)
            }
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            StartExamResponse(examLogId = data.getString("examLogId"))
        }

    suspend fun saveExamResult(
        examLogId: String,
        quizId: String,
        result: Boolean
    ): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.EXAM_RESULT,
            body = executor.jsonBody {
                put("examLogId", examLogId)
                put("quizId", quizId)
                put("result", result)
            }
        ) {
            ApiSuccessResponse()
        }

    suspend fun completeExam(examLogId: String): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.EXAM_COMPLETED,
            body = executor.jsonBody {
                put("examLogId", examLogId)
            }
        ) {
            ApiSuccessResponse()
        }
}