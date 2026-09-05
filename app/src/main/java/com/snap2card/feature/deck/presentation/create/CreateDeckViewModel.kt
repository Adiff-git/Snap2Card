package com.snap2card.feature.deck.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.usecase.CreateDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DeckCreationDestination { DETAIL, CAMERA, DOCUMENT, MANUAL }

@HiltViewModel
class CreateDeckViewModel @Inject constructor(
    private val createDeckUseCase: CreateDeckUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateDeckUiState>(CreateDeckUiState.Idle)
    val uiState: StateFlow<CreateDeckUiState> = _uiState.asStateFlow()

    fun createDeck(title: String, tag: String, destination: DeckCreationDestination) {
        if (title.isBlank()) {
            _uiState.value = CreateDeckUiState.Error("Title cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = CreateDeckUiState.Loading
            // NOTE: pre-existing bug — `tag` lands in `description`, not a tag field.
            // Leaving as-is here; flag separately since fixing it may need a use-case signature change.
            runCatching { createDeckUseCase(title, tag) }
                .onSuccess { deck -> _uiState.value = CreateDeckUiState.Success(deck.id, destination) }
                .onFailure { e -> _uiState.value = CreateDeckUiState.Error(e.message ?: "Failed to create deck") }
        }
    }

}
