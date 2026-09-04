package com.wanderlab.snap2card.controllers.serverconnection

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
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import com.wanderlab.snap2card.controllers.serverconnection.implements.insert.AccountInsert
import com.wanderlab.snap2card.controllers.serverconnection.implements.insert.CardInsert
import com.wanderlab.snap2card.controllers.serverconnection.implements.insert.CategoryInsert
import com.wanderlab.snap2card.controllers.serverconnection.implements.insert.ExamInsert
import com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve.AccountRetrieve
import com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve.CardRetrieve
import com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve.CategoryRetrieve
import com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve.ExamRetrieve
import com.wanderlab.snap2card.controllers.serverconnection.implements.update.AccountUpdate
import com.wanderlab.snap2card.controllers.serverconnection.implements.update.CardUpdate
import com.wanderlab.snap2card.controllers.serverconnection.implements.update.CategoryUpdate
import com.wanderlab.snap2card.controllers.serverconnection.interfaces.ServerConnectionApi

class ServerConnection(
    baseUrl: String,
    timeoutSeconds: Long = 30
) : ServerConnectionApi {

    private val executor = ApiExecutor(baseUrl, timeoutSeconds)

    private val accountInsert = AccountInsert(executor)
    private val accountRetrieve = AccountRetrieve(executor)
    private val accountUpdate = AccountUpdate(executor)
    private val cardInsert = CardInsert(executor)
    private val cardRetrieve = CardRetrieve(executor)
    private val cardUpdate = CardUpdate(executor)
    private val categoryInsert = CategoryInsert(executor)
    private val categoryRetrieve = CategoryRetrieve(executor)
    private val categoryUpdate = CategoryUpdate(executor)
    private val examInsert = ExamInsert(executor)
    private val examRetrieve = ExamRetrieve(executor)

    val isAuthenticated: Boolean
        get() = executor.isAuthenticated

    var token: String?
        get() = executor.token
        set(value) {
            executor.token = value
        }

    // ---------- Account insert ----------

    override suspend fun login(email: String, password: String): ApiResult<LoginResponse> =
        accountInsert.login(email, password)

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): ApiResult<RegisterResponse> = accountInsert.register(name, email, phone, password)

    override suspend fun logout(): ApiResult<ApiSuccessResponse> = accountInsert.logout()

    // ---------- Account retrieve ----------

    override suspend fun getAccount(): ApiResult<AccountResponse> = accountRetrieve.getAccount()

    override suspend fun getAvatar(): ApiResult<ByteArray> = accountRetrieve.getAvatar()

    override suspend fun getDailyLearnedCount(year: Int, month: Int, day: Int): ApiResult<DailyLearnedCountResponse> =
        accountRetrieve.getDailyLearnedCount(year, month, day)

    override suspend fun getMonthlyLearnedCount(): ApiResult<List<MonthlyLearnedEntry>> =
        accountRetrieve.getMonthlyLearnedCount()

    // ---------- Account update ----------

    override suspend fun updateAvatar(imageBytes: ByteArray, mimeType: String): ApiResult<ApiSuccessResponse> =
        accountUpdate.updateAvatar(imageBytes, mimeType)

    override suspend fun editAccount(request: EditAccountRequest): ApiResult<ApiSuccessResponse> =
        accountUpdate.editAccount(request)

    // ---------- Card insert ----------

    override suspend fun createCard(frontSide: String, backSide: String): ApiResult<CreateCardsResponse> =
        cardInsert.createCard(frontSide, backSide)

    override suspend fun createCardFromDocument(text: String): ApiResult<CreateCardsResponse> =
        cardInsert.createCardFromDocument(text)

    override suspend fun createCardFromPdf(pdfBytes: ByteArray): ApiResult<CreateCardsResponse> =
        cardInsert.createCardFromPdf(pdfBytes)

    override suspend fun categorizeCard(cardId: String, categoryIds: List<String>): ApiResult<ApiSuccessResponse> =
        cardInsert.categorizeCard(cardId, categoryIds)

    // ---------- Card retrieve ----------

    override suspend fun listCards(): ApiResult<CardListResponse> = cardRetrieve.listCards()

    override suspend fun retrieveCards(ids: List<String>): ApiResult<List<CardInfo>> =
        cardRetrieve.retrieveCards(ids)

    // ---------- Card update ----------

    override suspend fun editCard(request: EditCardRequest): ApiResult<ApiSuccessResponse> =
        cardUpdate.editCard(request)

    override suspend fun deleteCard(id: String): ApiResult<ApiSuccessResponse> =
        cardUpdate.deleteCard(id)

    // ---------- Category insert ----------

    override suspend fun createCategory(name: String): ApiResult<CreateCategoryResponse> =
        categoryInsert.createCategory(name)

    override suspend fun categorizeCards(categoryId: String, cardIds: List<String>): ApiResult<ApiSuccessResponse> =
        categoryInsert.categorizeCards(categoryId, cardIds)

    // ---------- Category retrieve ----------

    override suspend fun listCategories(): ApiResult<CategoryListResponse> =
        categoryRetrieve.listCategories()

    override suspend fun retrieveCategory(id: String): ApiResult<CategoryDetail> =
        categoryRetrieve.retrieveCategory(id)

    override suspend fun getCategoryLogs(categoryId: String): ApiResult<List<ExamLogEntry>> =
        categoryRetrieve.getCategoryLogs(categoryId)

    override suspend fun getRecentCategories(n: Int?): ApiResult<List<RecentCategoryEntry>> =
        categoryRetrieve.getRecentCategories(n)

    // ---------- Category update ----------

    override suspend fun editCategory(request: EditCategoryRequest): ApiResult<ApiSuccessResponse> =
        categoryUpdate.editCategory(request)

    override suspend fun deleteCategory(id: String): ApiResult<ApiSuccessResponse> =
        categoryUpdate.deleteCategory(id)

    // ---------- Exam insert ----------

    override suspend fun createExam(categoryId: String): ApiResult<CreateExamResponse> =
        examInsert.createExam(categoryId)

    override suspend fun startExam(examId: String): ApiResult<StartExamResponse> =
        examInsert.startExam(examId)

    override suspend fun saveExamResult(
        examLogId: String,
        quizId: String,
        result: Boolean
    ): ApiResult<ApiSuccessResponse> = examInsert.saveExamResult(examLogId, quizId, result)

    override suspend fun completeExam(examLogId: String): ApiResult<ApiSuccessResponse> =
        examInsert.completeExam(examLogId)

    // ---------- Exam retrieve ----------

    override suspend fun reviewExam(examId: String): ApiResult<ExamReviewResponse> =
        examRetrieve.reviewExam(examId)

    override suspend fun listExams(): ApiResult<List<ExamInfo>> = examRetrieve.listExams()
}