package com.snap2card.feature.study.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.study.domain.model.ReviewResult
import com.snap2card.feature.study.domain.usecase.RecordReviewUseCase
import com.snap2card.feature.study.domain.usecase.StudyDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val studyDeckUseCase: StudyDeckUseCase,
    private val recordReviewUseCase: RecordReviewUseCase,
) : ViewModel() {

    private val deckId: String = checkNotNull(savedStateHandle["deckId"])

    private val _uiState = MutableStateFlow<StudyUiState>(StudyUiState.Loading)
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var gotItCount = 0

    init { loadDeck() }

    private fun loadDeck() {
        viewModelScope.launch {
            runCatching { studyDeckUseCase(deckId).first() }
                .onSuccess { cards ->
                    if (cards.isEmpty()) _uiState.value = StudyUiState.Completed
                    else _uiState.value = StudyUiState.Studying(
                        cards = cards, currentIndex = 0, isRevealed = false, masteryPercent = 0
                    )
                }
                .onFailure { e -> _uiState.value = StudyUiState.Error(e.message ?: "Load failed") }
        }
    }

    fun revealCard() {
        val s = _uiState.value as? StudyUiState.Studying ?: return
        _uiState.value = s.copy(isRevealed = true)
    }

    fun recordAnswer(result: ReviewResult) {
        val s = _uiState.value as? StudyUiState.Studying ?: return
        val card = s.cards[s.currentIndex]
        viewModelScope.launch { recordReviewUseCase(card.id, deckId, result) }
        if (result == ReviewResult.GOT_IT) gotItCount++
        val next = s.currentIndex + 1
        if (next >= s.cards.size) {
            _uiState.value = StudyUiState.Completed
        } else {
            val mastery = (gotItCount * 100) / s.cards.size
            _uiState.value = s.copy(currentIndex = next, isRevealed = false, masteryPercent = mastery)
        }
    }
}
