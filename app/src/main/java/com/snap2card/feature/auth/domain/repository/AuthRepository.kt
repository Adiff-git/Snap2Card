package com.snap2card.feature.auth.domain.repository

import com.snap2card.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Auth repository interface — abstracts authentication and session management.
 * The rest of the app never needs to know which auth mechanism is used.
 */
interface AuthRepository {
    /** Returns the currently signed-in user, or null if signed out. */
    val currentUser: Flow<User?>

    /** Sign in using email and password. Returns the session token. */
    suspend fun loginWithEmail(email: String, password: String): Result<String>

    /** Sign in using a Google ID token obtained from Credential Manager (future). */
    suspend fun signInWithGoogle(idToken: String): Result<User>

    /** Clear local session and invalidate tokens. */
    suspend fun signOut()

    /** True if a valid session exists in DataStore. */
    suspend fun isSessionValid(): Boolean
}
