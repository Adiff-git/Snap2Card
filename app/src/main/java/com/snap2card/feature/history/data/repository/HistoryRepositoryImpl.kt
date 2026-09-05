package com.snap2card.feature.history.data.repository

import com.snap2card.feature.account.data.remote.AccountApiService
import com.snap2card.feature.deck.data.remote.CategoryApiService
import com.snap2card.feature.history.data.mapper.toDailyCounts
import com.snap2card.feature.history.data.mapper.toDomain
import com.snap2card.feature.history.domain.model.HistorySession
import com.snap2card.feature.history.domain.model.StudyActivity
import com.snap2card.feature.history.domain.repository.HistoryRepository
import com.snap2card.feature.study.domain.repository.StudyRepository
import com.snap2card.feature.study.domain.usecase.CalculateStreakUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApiService,
    private val accountApi: AccountApiService,
    private val studyRepository: StudyRepository,
    private val calculateStreak: CalculateStreakUseCase,
) : HistoryRepository {

    override suspend fun getActivity(): Result<StudyActivity> = runCatching {
        val monthlyDto = accountApi.getMonthlyLearnedCount().data.orEmpty()
        val dailyCounts = monthlyDto.toDailyCounts()
        val reviews = studyRepository.getAllReviews().first()

        StudyActivity(
            currentStreakDays = calculateStreak(reviews),   // local, reliable
            cardsThisMonth = dailyCounts.sumOf { it.count }, // backend-derived, fine for a monthly count
            dailyCounts = dailyCounts,
        )
    }

    override suspend fun getHistory(page: Int, limit: Int): Result<List<HistorySession>> = runCatching {
        val categories = categoryApi.getCategories().data?.categories.orEmpty()
        categories.flatMap { category ->
            categoryApi.getCategoryLogs(category.id).data.orEmpty()
                .map { it.toDomain(categoryName = category.name) }
        }.sortedByDescending { it.completedAt }
    }
}