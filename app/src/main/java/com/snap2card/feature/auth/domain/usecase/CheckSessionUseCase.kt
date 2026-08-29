package com.snap2card.feature.auth.domain.usecase

import com.snap2card.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Checks whether a valid session exists in DataStore.
 * Used by the Splash screen to decide if the user should be routed
 * to Home (valid session) or Login (no session / expired).
 */
class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = authRepository.isSessionValid()
}
