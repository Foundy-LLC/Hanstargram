package io.foundy.hanstargram.view.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import io.foundy.hanstargram.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = viewModelScope.launch { searchUser("") }

    fun searchUser(name: String) {
        job?.cancel()
        job = viewModelScope.launch {
            UserRepository.searchUser(name).cachedIn(viewModelScope).collectLatest { pagingData ->
                _uiState.update { uiState ->
                    uiState.copy(pagingData = pagingData.map { it.toSearchItemUiState() })
                }
            }
        }
    }
}