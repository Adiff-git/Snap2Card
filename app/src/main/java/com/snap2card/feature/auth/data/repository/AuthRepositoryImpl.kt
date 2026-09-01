package com.snap2card.feature.auth.data.repository

import com.snap2card.core.datastore.UserPreferencesDataStore
import com.snap2card.feature.auth.data.mapper.toDomain
import com.snap2card.feature.auth.data.remote.AuthApiService
import com.snap2card.feature.auth.data.remote.dto.GoogleAuthRequest
import com.snap2card.feature.auth.data.remote.dto.LoginRequest
import com.snap2card.feature.auth.domain.model.User
import com.snap2card.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userPreferencesDataStore: UserPreferencesDataStore,
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    override suspend fun loginWithEmail(email: String, password: String): Result<String> = runCatching {
        val response = authApiService.login(LoginRequest(email, password))
        val token = response.data.token
        // Backend returns only a token — save it as both access and refresh for now.
        // userId is unknown at login time; store email as fallback identifier.
        userPreferencesDataStore.saveSession(
            accessToken = token,
            refreshToken = token,
            userId = email,
        )
        token
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val response = authApiService.signInWithGoogle(GoogleAuthRequest(idToken))
        val user = response.toDomain()
        userPreferencesDataStore.saveSession(response.accessToken, response.refreshToken, response.userId)
        _currentUser.value = user
        user
    }

    override suspend fun signOut() {
        userPreferencesDataStore.clearSession()
        _currentUser.value = null
    }

    override suspend fun isSessionValid(): Boolean {
        return userPreferencesDataStore.accessToken.first() != null
    }
}
