package io.foundy.hanstargram.view.posting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.foundy.data.model.PostDto
import io.foundy.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PostingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PostDto())

    fun uploadContent(
        content: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            val result = PostRepository.uploadPost(content, imageUrl)
            if (result.isFailure) {
                println("uploadPost Error")
            }
        }
    }
}