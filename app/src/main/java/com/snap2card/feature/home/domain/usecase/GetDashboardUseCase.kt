package com.snap2card.feature.home.domain.usecase

import com.snap2card.feature.home.domain.model.DashboardData
import com.snap2card.feature.home.domain.repository.DashboardRepository
import javax.inject.Inject

/**
 * Fetches the aggregate dashboard data for the Home screen.
 * Wraps the repository call in a Result for safe error handling in the ViewModel.
 */
class GetDashboardUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository,
) {
    suspend operator fun invoke(): Result<DashboardData> = runCatching {
        dashboardRepository.getDashboard()
    }
}
