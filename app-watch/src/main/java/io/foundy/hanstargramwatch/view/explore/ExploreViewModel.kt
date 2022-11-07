package io.foundy.hanstargramwatch.view.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import io.foundy.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            UserRepository.searchUser("").cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _uiState.update { uiState ->
                        uiState.copy(pagingData = pagingData.map { it.toUiState() })
                    }
                }
        }
    }
}