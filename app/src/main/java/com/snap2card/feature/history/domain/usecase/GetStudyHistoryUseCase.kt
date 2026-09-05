package com.snap2card.feature.history.domain.usecase

import com.snap2card.feature.history.domain.model.HistorySession
import com.snap2card.feature.history.domain.repository.HistoryRepository
import javax.inject.Inject

class GetStudyHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository,
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 20): Result<List<HistorySession>> =
        repository.getHistory(page, limit)
}