package com.snap2card.feature.history.data.repository

import com.snap2card.feature.account.data.remote.AccountApiService
import com.snap2card.feature.deck.data.remote.CategoryApiService
import com.snap2card.feature.history.data.mapper.toDomain
import com.snap2card.feature.history.domain.model.HistorySession
import com.snap2card.feature.history.domain.model.StudyActivity
import com.snap2card.feature.history.domain.repository.HistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApiService,
    private val accountApi: AccountApiService,
) : HistoryRepository {

    override suspend fun getActivity(): Result<StudyActivity> = runCatching {
        accountApi.getMonthlyLearnedCount().data?.toDomain() ?: error("Missing activity data in response")
    }

    override suspend fun getHistory(page: Int, limit: Int): Result<List<HistorySession>> = runCatching {
        val categories = categoryApi.getCategories().data?.categories.orEmpty()
        categories.flatMap { category ->
            categoryApi.getCategoryLogs(category.id).data.orEmpty()
                .map { it.toDomain(categoryName = category.name) }
        }.sortedByDescending { it.completedAt }
    }
}