package com.snap2card.feature.home.domain.repository

import com.snap2card.feature.home.domain.model.DashboardData

/**
 * Repository interface for Home dashboard data.
 * The implementation can be a fake (mock data) or real (API-backed).
 */
interface DashboardRepository {
    suspend fun getDashboard(): DashboardData
}
