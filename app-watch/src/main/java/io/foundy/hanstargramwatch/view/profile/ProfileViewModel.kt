package io.foundy.hanstargramwatch.view.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.foundy.data.repository.UserRepository
import io.foundy.domain.model.UserDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun loadUserProfile(userUuid: String) {
        viewModelScope.launch {
            val result = UserRepository.getUserDetail(userUuid)
            if (result.isSuccess) {
                _uiState.update { it.copy(userDetail = result.getOrNull()!!) }
            } else {
                _uiState.update { it.copy(userMessage = result.exceptionOrNull()!!.message) }
            }
        }
    }

    fun toggleFollow() {
        val userDetail = _uiState.value.userDetail
        check(userDetail != null)
        viewModelScope.launch {
            if (userDetail.isCurrentUserFollowing) {
                unfollow(userDetail)
            } else {
                follow(userDetail)
            }
        }
    }

    private suspend fun follow(userDetail: UserDetail) {
        val result = UserRepository.follow(userDetail.uuid)
        if (result.isSuccess) {
            _uiState.update {
                it.copy(
                    userDetail = userDetail.copy(
                        followersCount = userDetail.followersCount + 1,
                        isCurrentUserFollowing = true
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(userMessage = result.exceptionOrNull()!!.message)
            }
        }
    }

    private suspend fun unfollow(userDetail: UserDetail) {
        val result = UserRepository.unfollow(userDetail.uuid)
        if (result.isSuccess) {
            _uiState.update {
                it.copy(
                    userDetail = userDetail.copy(
                        followersCount = userDetail.followersCount - 1,
                        isCurrentUserFollowing = false
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(userMessage = result.exceptionOrNull()!!.message)
            }
        }
    }

    fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }
}