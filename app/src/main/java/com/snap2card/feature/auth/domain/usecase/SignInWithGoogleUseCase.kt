package com.snap2card.feature.auth.domain.usecase

import com.snap2card.feature.auth.domain.model.User
import com.snap2card.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> =
        authRepository.signInWithGoogle(idToken)
}
