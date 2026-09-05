package com.snap2card.feature.study.domain.usecase

import com.snap2card.feature.study.domain.model.ExamReviewDetail
import com.snap2card.feature.study.domain.repository.StudyRepository
import javax.inject.Inject

class GetExamReviewDetailUseCase @Inject constructor(
    private val studyRepository: StudyRepository,
) {
    suspend operator fun invoke(examLogId: String): Result<ExamReviewDetail> =
        studyRepository.getExamReviewDetail(examLogId)
}