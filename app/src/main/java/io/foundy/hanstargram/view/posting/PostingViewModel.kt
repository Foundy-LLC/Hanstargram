package io.foundy.hanstargram.view.posting

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.foundy.data.repository.PostRepository
import io.foundy.hanstargram.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PostingUiState())
    val uiState = _uiState.asStateFlow()

    fun selectImage(bitmap: Bitmap) {
        _uiState.update { it.copy(selectedImage = bitmap) }
    }

    fun uploadContent(content: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = PostRepository.uploadPost(content, uiState.value.selectedImage!!)
            if (result.isSuccess) {
                _uiState.update { it.copy(successToUpload = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        userMessage = R.string.failed_to_upload,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}