package io.foundy.hanstargram.view.posting

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.StringRes

data class PostingUiState(
    val selectedImage: Bitmap? = null,
    @StringRes
    val userMessage: Int? = null,
    val isLoading: Boolean = false,
    val successToUpload: Boolean = false
)
