package com.snap2card.feature.account.domain.repository

import com.snap2card.feature.auth.domain.model.User
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.study.domain.model.ReviewRecord
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    /** Signed-in user + locally stored birthday, combined. Null user = signed out. */
    fun getProfile(): Flow<Pair<User?, Long?>>
    suspend fun updateBirthday(birthdayMillis: Long?)

    fun getStreak(): Flow<Int>
    fun getReviewHistory(): Flow<List<ReviewRecord>>
    fun getDeckHistory(): Flow<List<Deck>>
}