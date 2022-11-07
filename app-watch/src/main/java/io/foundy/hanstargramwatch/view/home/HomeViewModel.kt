package io.foundy.hanstargramwatch.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import io.foundy.data.repository.PostRepository
import io.foundy.hanstargramwatch.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            PostRepository.getPostsByFollower().cachedIn(viewModelScope)
                .collectLatest { pagingData ->
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

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}