package io.foundy.hanstargram.view.profile.edit

sealed class ProfileEditUiState {
    object None : ProfileEditUiState()
    object Loading : ProfileEditUiState()
    object SuccessToSave : ProfileEditUiState()
    data class FailedToSave(val exception: Throwable) : ProfileEditUiState()
}

