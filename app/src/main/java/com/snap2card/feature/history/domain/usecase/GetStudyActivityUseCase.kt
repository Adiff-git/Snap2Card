package com.snap2card.feature.history.domain.usecase

import com.snap2card.feature.history.domain.model.StudyActivity
import com.snap2card.feature.history.domain.repository.HistoryRepository
import javax.inject.Inject

class GetStudyActivityUseCase @Inject constructor(
    private val repository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<StudyActivity> = repository.getActivity()
}