package com.snap2card.feature.account.presentation

import com.snap2card.feature.auth.domain.model.User
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.study.domain.model.ReviewRecord

sealed class AccountUiState {
    data object Loading : AccountUiState()
    data class Success(
        val user: User,
        val birthday: Long?,
        val streak: Int,
        val decks: List<Deck>,
        val reviews: List<ReviewRecord>,
    ) : AccountUiState()
    data object SignedOut : AccountUiState()
}