package com.snap2card.feature.account.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.snap2card.feature.account.domain.repository.AccountRepository
import com.snap2card.feature.auth.domain.model.User
import com.snap2card.feature.auth.domain.repository.AuthRepository
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val studyRepository: StudyRepository,
    private val deckRepository: DeckRepository,
    private val dataStore: DataStore<Preferences>,
) : AccountRepository {

    companion object {
        private val KEY_BIRTHDAY = longPreferencesKey("account_birthday")
    }

    override fun getProfile(): Flow<Pair<User?, Long?>> =
        combine(authRepository.currentUser, dataStore.data) { user, prefs ->
            user to prefs[KEY_BIRTHDAY]
        }

    override suspend fun updateBirthday(birthdayMillis: Long?) {
        dataStore.edit { prefs ->
            if (birthdayMillis != null) prefs[KEY_BIRTHDAY] = birthdayMillis
            else prefs.remove(KEY_BIRTHDAY)
        }
    }

    override fun getStreak(): Flow<Int> = studyRepository.getAllReviews().map { reviews ->
        val zone = ZoneId.systemDefault()
        val reviewedDates = reviews
            .map { Instant.ofEpochMilli(it.reviewedAt).atZone(zone).toLocalDate() }
            .toSortedSet()

        if (reviewedDates.isEmpty()) return@map 0
        val today = LocalDate.now(zone)
        var cursor = when (reviewedDates.last()) {
            today -> today
            today.minusDays(1) -> today.minusDays(1)
            else -> return@map 0
        }
        var streak = 0
        while (reviewedDates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        streak
    }

    override fun getReviewHistory(): Flow<List<ReviewRecord>> = studyRepository.getAllReviews()

    override fun getDeckHistory(): Flow<List<Deck>> = deckRepository.getDecks()
}

/**
 * Fake auth for local dev — bypasses real Google Sign-In so screens
 * downstream of login (Account, Settings, etc.) can be built/tested
 * without a working auth flow yet.
 * NOT TO BE COMMITTED for final submission.
 */
@Singleton
class FakeAuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val fakeUser = User(
        id = "test-user-1",
        email = "test@snap2card.dev",
        displayName = "Test User",
        photoUrl = null, // or a real image URL if you want to test AsyncImage too
    )

    private val _currentUser = MutableStateFlow<User?>(fakeUser)
    override val currentUser: StateFlow<User?> = _currentUser

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        _currentUser.value = fakeUser
        return Result.success(fakeUser)
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<String> {
        _currentUser.value = fakeUser
        return Result.success("FAKE_TOKEN")
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun isSessionValid(): Boolean = _currentUser.value != null
}