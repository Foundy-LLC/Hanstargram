package io.foundy.hanstargramwatch.view.welcome

sealed class WelcomeUiState {
    object None : WelcomeUiState()
    object Loading : WelcomeUiState()
    object SuccessToSave : WelcomeUiState()
    data class FailedToSave(val exception: Throwable) : WelcomeUiState()
}
