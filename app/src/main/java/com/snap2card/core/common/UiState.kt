package com.snap2card.core.common

/**
 * Generic UI state wrapper used across all ViewModels.
 * Keeps UI layer decoupled from data-layer result types.
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}
