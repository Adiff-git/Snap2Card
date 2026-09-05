package com.snap2card.feature.history.domain.repository

import com.snap2card.feature.history.domain.model.HistorySession
import com.snap2card.feature.history.domain.model.StudyActivity

interface HistoryRepository {
    suspend fun getActivity(): Result<StudyActivity>
    suspend fun getHistory(page: Int, limit: Int): Result<List<HistorySession>>
}