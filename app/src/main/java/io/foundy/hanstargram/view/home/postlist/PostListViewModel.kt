package io.foundy.hanstargram.view.home.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.foundy.hanstargram.R
import io.foundy.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()

    private var bounded = false

    fun bind(targetUserUuid: String?, initPostPagingData: PagingData<PostItemUiState>?) {
        if (bounded) return
        bounded = true
        if (initPostPagingData != null) {
            _uiState.update { it.copy(pagingData = initPostPagingData) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val pagingFlow = if (targetUserUuid != null) {
                PostRepository.getPostDetailsByUser(targetUserUuid)
            } else {
                PostRepository.getHomeFeeds()
            }
            pagingFlow.cachedIn(viewModelScope)
                .collect { pagingData ->
                    _uiState.update { uiState ->
                        uiState.copy(pagingData = pagingData.map { it.toUiState() })
                    }
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