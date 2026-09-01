package com.snap2card.feature.account.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.account.domain.usecase.GetAccountProfileUseCase
import com.snap2card.feature.account.domain.usecase.GetDeckHistoryUseCase
import com.snap2card.feature.account.domain.usecase.GetReviewHistoryUseCase
import com.snap2card.feature.account.domain.usecase.GetStreakUseCase
import com.snap2card.feature.account.domain.usecase.UpdateBirthdayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    getAccountProfile: GetAccountProfileUseCase,
    getStreak: GetStreakUseCase,
    getDeckHistory: GetDeckHistoryUseCase,
    getReviewHistory: GetReviewHistoryUseCase,
    private val updateBirthday: UpdateBirthdayUseCase,
) : ViewModel() {

    val uiState = combine(
        getAccountProfile(),
        getStreak(),
        getDeckHistory(),
        getReviewHistory(),
    ) { (user, birthday), streak, decks, reviews ->
        if (user == null) AccountUiState.SignedOut
        else AccountUiState.Success(user, birthday, streak, decks, reviews)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AccountUiState.Loading)

    fun setBirthday(millis: Long?) {
        viewModelScope.launch { updateBirthday(millis) }
    }
}