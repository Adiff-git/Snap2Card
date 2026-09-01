package com.snap2card.feature.account.domain.usecase

import com.snap2card.feature.account.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountProfileUseCase @Inject constructor(private val repo: AccountRepository) {
    operator fun invoke() = repo.getProfile()
}

class UpdateBirthdayUseCase @Inject constructor(private val repo: AccountRepository) {
    suspend operator fun invoke(birthdayMillis: Long?) = repo.updateBirthday(birthdayMillis)
}

class GetStreakUseCase @Inject constructor(private val repo: AccountRepository) {
    operator fun invoke() = repo.getStreak()
}

class GetReviewHistoryUseCase @Inject constructor(private val repo: AccountRepository) {
    operator fun invoke() = repo.getReviewHistory()
}

class GetDeckHistoryUseCase @Inject constructor(private val repo: AccountRepository) {
    operator fun invoke() = repo.getDeckHistory()
}