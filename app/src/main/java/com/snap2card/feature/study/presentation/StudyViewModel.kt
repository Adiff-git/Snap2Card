package com.snap2card.feature.study.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.study.data.mapper.toCard
import com.snap2card.feature.study.domain.model.ReviewResult
import com.snap2card.feature.study.domain.repository.StudyRepository
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
    private val studyRepository: StudyRepository,
    private val recordReviewUseCase: RecordReviewUseCase,
) : ViewModel() {

    private val deckId: String = checkNotNull(savedStateHandle["deckId"])
    // ASSUMPTION: deckId doubles as the server-side categoryId needed by createExam().
    // Confirm this — if decks and categories are separate client-side concepts,
    // pass/store categoryId separately instead of reusing deckId.
    private val categoryId: String = deckId

    private val _uiState = MutableStateFlow<StudyUiState>(StudyUiState.Loading)
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var examLogId: String? = null
    private var gotItCount = 0

    init { startExamSession() }

    private fun startExamSession() {
        viewModelScope.launch {
            _uiState.value = StudyUiState.Loading

            val examId = studyRepository.createExam(categoryId)
                .onFailure { e -> _uiState.value = StudyUiState.Error(e.message ?: "Failed to create exam") }
                .getOrNull() ?: return@launch

            val session = studyRepository.startExam(examId)
                .onFailure { e -> _uiState.value = StudyUiState.Error(e.message ?: "Failed to start exam") }
                .getOrNull() ?: return@launch
            examLogId = session.examLogId

            studyRepository.getExamReview(examId)
                .onSuccess { quizzes ->
                    _uiState.value = if (quizzes.isEmpty()) {
                        StudyUiState.Completed
                    } else {
                        StudyUiState.Studying(
                            cards = quizzes.map { it.toCard(deckId) },
                            currentIndex = 0,
                            isRevealed = false,
                            masteryPercent = 0,
                        )
                    }
                }
                .onFailure { e -> _uiState.value = StudyUiState.Error(e.message ?: "Failed to load review") }
        }
    }

    fun revealCard() {
        val s = _uiState.value as? StudyUiState.Studying ?: return
        _uiState.value = s.copy(isRevealed = true)
    }

    fun recordAnswer(result: ReviewResult) {
        val s = _uiState.value as? StudyUiState.Studying ?: return
        val card = s.cards[s.currentIndex]
        val logId = examLogId

        viewModelScope.launch {
            recordReviewUseCase(card.id, deckId, result) // local SRS bookkeeping, unchanged
            if (logId != null) {
                studyRepository.submitResult(
                    examLogId = logId,
                    quizId = card.id, // holds the quizId, per toCard() above
                    result = result == ReviewResult.GOT_IT,
                )
            }
        }

        if (result == ReviewResult.GOT_IT) gotItCount++
        val next = s.currentIndex + 1
        if (next >= s.cards.size) {
            finishSession()
        } else {
            val mastery = (gotItCount * 100) / s.cards.size
            _uiState.value = s.copy(currentIndex = next, isRevealed = false, masteryPercent = mastery)
        }
    }

    private fun finishSession() {
        val logId = examLogId
        viewModelScope.launch {
            if (logId != null) studyRepository.completeExam(logId)
            _uiState.value = StudyUiState.Completed
        }
    }
}
