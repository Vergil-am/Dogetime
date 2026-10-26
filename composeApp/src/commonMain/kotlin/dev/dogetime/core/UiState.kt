package dev.dogetime.core

import kotlinx.coroutines.CancellationException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
}

/** Runs [block] and wraps the outcome in a [UiState]. Cancellation is rethrown, never swallowed. */
suspend fun <T> runForUiState(block: suspend () -> T): UiState<T> = try {
    UiState.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    UiState.Error(e.message ?: "Something went wrong")
}
