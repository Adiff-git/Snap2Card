package com.snap2card.feature.auth.domain.usecase

import com.snap2card.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for email/password login.
 * Returns Result<String> containing the session token on success.
 */
class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<String> =
        authRepository.loginWithEmail(email, password)
}
