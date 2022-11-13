package io.foundy.hanstargram.view.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import io.foundy.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState = _uiState.asStateFlow()

    private var didBind = false

    fun bind(userUuid: String, type: UserListPageType) {
        check(!didBind)
        didBind = true
        viewModelScope.launch {
            val pagingFlow = when(type) {
                UserListPageType.FOLLOWING -> UserRepository.getFollowingUsersPaging(userUuid)
                UserListPageType.FOLLOWER ->  UserRepository.getFollowersPaging(userUuid)
            }
            pagingFlow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _uiState.update { uiState ->
                        uiState.copy(pagingData = pagingData.map { it.toUiState() })
                    }
                }
        }
    }
}