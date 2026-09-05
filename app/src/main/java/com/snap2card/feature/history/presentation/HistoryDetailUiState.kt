package com.snap2card.feature.history.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.study.domain.model.ExamReviewDetail
import com.snap2card.feature.study.domain.usecase.GetExamReviewDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryDetailUiState {
    data object Loading : HistoryDetailUiState()
    data class Loaded(val detail: ExamReviewDetail) : HistoryDetailUiState()
    data class Error(val message: String) : HistoryDetailUiState()
}

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val getExamReviewDetailUseCase: GetExamReviewDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val examLogId: String = checkNotNull(savedStateHandle["logId"])

    private val _uiState = MutableStateFlow<HistoryDetailUiState>(HistoryDetailUiState.Loading)
    val uiState: StateFlow<HistoryDetailUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HistoryDetailUiState.Loading
            getExamReviewDetailUseCase(examLogId)
                .onSuccess { _uiState.value = HistoryDetailUiState.Loaded(it) }
                .onFailure { e -> _uiState.value = HistoryDetailUiState.Error(e.message ?: "Failed to load detail") }
        }
    }
}