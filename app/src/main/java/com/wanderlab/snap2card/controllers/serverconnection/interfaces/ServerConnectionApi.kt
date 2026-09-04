package com.wanderlab.snap2card.controllers.serverconnection.interfaces

import com.wanderlab.snap2card.controllers.serverconnection.definitions.AccountResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CardInfo
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CardListResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CategoryDetail
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CategoryListResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateCardsResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateCategoryResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateExamResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.DailyLearnedCountResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditAccountRequest
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditCardRequest
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditCategoryRequest
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ExamInfo
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ExamLogEntry
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ExamReviewResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.LoginResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.MonthlyLearnedEntry
import com.wanderlab.snap2card.controllers.serverconnection.definitions.RecentCategoryEntry
import com.wanderlab.snap2card.controllers.serverconnection.definitions.RegisterResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.StartExamResponse

interface ServerConnectionApi {

    // ---------- Account ----------

    suspend fun login(email: String, password: String): ApiResult<LoginResponse>
    suspend fun register(name: String, email: String, phone: String, password: String): ApiResult<RegisterResponse>
    suspend fun getAccount(): ApiResult<AccountResponse>
    suspend fun getAvatar(): ApiResult<ByteArray>
    suspend fun updateAvatar(imageBytes: ByteArray, mimeType: String): ApiResult<ApiSuccessResponse>
    suspend fun editAccount(request: EditAccountRequest): ApiResult<ApiSuccessResponse>
    suspend fun logout(): ApiResult<ApiSuccessResponse>
    suspend fun getDailyLearnedCount(year: Int, month: Int, day: Int): ApiResult<DailyLearnedCountResponse>
    suspend fun getMonthlyLearnedCount(): ApiResult<List<MonthlyLearnedEntry>>

    // ---------- Cards ----------

    suspend fun createCard(frontSide: String, backSide: String): ApiResult<CreateCardsResponse>
    suspend fun createCardFromDocument(text: String): ApiResult<CreateCardsResponse>
    suspend fun createCardFromPdf(pdfBytes: ByteArray): ApiResult<CreateCardsResponse>
    suspend fun editCard(request: EditCardRequest): ApiResult<ApiSuccessResponse>
    suspend fun deleteCard(id: String): ApiResult<ApiSuccessResponse>
    suspend fun listCards(): ApiResult<CardListResponse>
    suspend fun retrieveCards(ids: List<String>): ApiResult<List<CardInfo>>
    suspend fun categorizeCard(cardId: String, categoryIds: List<String>): ApiResult<ApiSuccessResponse>

    // ---------- Categories ----------

    suspend fun createCategory(name: String): ApiResult<CreateCategoryResponse>
    suspend fun editCategory(request: EditCategoryRequest): ApiResult<ApiSuccessResponse>
    suspend fun deleteCategory(id: String): ApiResult<ApiSuccessResponse>
    suspend fun listCategories(): ApiResult<CategoryListResponse>
    suspend fun retrieveCategory(id: String): ApiResult<CategoryDetail>
    suspend fun categorizeCards(categoryId: String, cardIds: List<String>): ApiResult<ApiSuccessResponse>
    suspend fun getCategoryLogs(categoryId: String): ApiResult<List<ExamLogEntry>>
    suspend fun getRecentCategories(n: Int? = null): ApiResult<List<RecentCategoryEntry>>

    // ---------- Exams ----------

    suspend fun createExam(categoryId: String): ApiResult<CreateExamResponse>
    suspend fun startExam(examId: String): ApiResult<StartExamResponse>
    suspend fun saveExamResult(examLogId: String, quizId: String, result: Boolean): ApiResult<ApiSuccessResponse>
    suspend fun reviewExam(examId: String): ApiResult<ExamReviewResponse>
    suspend fun completeExam(examLogId: String): ApiResult<ApiSuccessResponse>
    suspend fun listExams(): ApiResult<List<ExamInfo>>
}