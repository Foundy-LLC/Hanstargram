package io.foundy.hanstargram.view.home.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import io.foundy.hanstargram.R
import io.foundy.hanstargram.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            PostRepository.getPostsByFollower().cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _uiState.update { it.copy(pagingData = pagingData) }
                }
        }
    }

    fun toggleLike(postUuid: String) {
        viewModelScope.launch {
            val result = PostRepository.toggleLike(postUuid)
            if (result.isFailure) {
                _uiState.update { it.copy(userMessage = R.string.failed_to_toggle_like_button) }
            }
        }
    }

    fun showPostOptionBottomSheet(postItemUiState: PostItemUiState) {
        _uiState.update { it.copy(selectedPostItem = postItemUiState) }
    }

    fun deleteSelectedPost(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val postItem = _uiState.value.selectedPostItem
            check(postItem != null)
            val result = PostRepository.deletePost(postItem.uuid)

            if (result.isSuccess) {
                onDeleted()
            }
            _uiState.update {
                it.copy(
                    userMessage = if (result.isSuccess) {
                        R.string.post_deleted
                    } else {
                        R.string.failed_to_delete_post
                    }
                )
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}