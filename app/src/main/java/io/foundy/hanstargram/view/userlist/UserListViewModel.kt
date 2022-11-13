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

    fun bindAsFollowing(followerUuid: String) {
        checkDidNotBind()
        didBind = true
        viewModelScope.launch {
            UserRepository.getFollowingUserPaging(followerUuid)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _uiState.update { uiState ->
                        uiState.copy(pagingData = pagingData.map { it.toUiState() })
                    }
                }
        }
    }

    fun bindAsFollower() {
        checkDidNotBind()
        didBind = true

    }

    private fun checkDidNotBind() {
        check(!didBind)
    }
}