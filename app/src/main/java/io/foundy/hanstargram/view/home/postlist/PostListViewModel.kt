package io.foundy.hanstargram.view.home.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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
}