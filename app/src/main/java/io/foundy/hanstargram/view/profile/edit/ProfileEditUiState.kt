package io.foundy.hanstargram.view.profile.edit

import android.graphics.Bitmap

data class ProfileEditUiState (
    val name: String = "",
    val selectedImageBitmap: Bitmap? = null,
    val isImageChanged: Boolean = false,
    val introduce: String = "",
    val successToSave: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
)


