package io.foundy.hanstargram.view.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import io.foundy.data.repository.PostRepository
import io.foundy.data.repository.UserRepository
import io.foundy.domain.model.UserDetail
import io.foundy.hanstargram.view.home.postlist.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    fun bindProfile(targetUuid: String) {
        loadPostByUser(targetUuid)
        getProfileDetail(targetUuid)
    }

    /* Post Ui State 구역 */
    private val _profilePostUiState = MutableStateFlow(ProfilePostUiState())
    val profilePostUiState = _profilePostUiState.asStateFlow()

    private fun loadPostByUser(targetUuid: String) {
        viewModelScope.launch {
            PostRepository.getPostDetailsByUser(targetUuid).cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _profilePostUiState.update { profilePostUiState ->
                        profilePostUiState.copy(pagingData = pagingData.map { it.toUiState() })
                    }
                }
        }
    }

    /* UserDetail Ui State 구역 */
    private val _profileDetailUiState: MutableStateFlow<ProfileDetailUiState> =
        MutableStateFlow(ProfileDetailUiState())
    val profileDetailUiState = _profileDetailUiState.asStateFlow()

    fun getProfileDetail(targetUuid: String) {
        _profileDetailUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            UserRepository.getUserDetail(targetUuid)
                .onSuccess { userDetails ->
                    _profileDetailUiState.update {
                        it.copy(userDetail = userDetails, isLoading = false)
                    }
                }
                .onFailure { e ->
                    _profileDetailUiState.update {
                        it.copy(userMessage = e.localizedMessage, isLoading = false)
                    }
                }
        }
    }

    fun toggleFollow() {
        val userDetail = _profileDetailUiState.value.userDetail
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
            _profileDetailUiState.update {
                it.copy(
                    userDetail = userDetail.copy(
                        followersCount = userDetail.followersCount + 1,
                        isCurrentUserFollowing = true
                    )
                )
            }
        } else {
            _profileDetailUiState.update {
                it.copy(userMessage = result.exceptionOrNull()!!.message)
            }
        }
    }

    private suspend fun unfollow(userDetail: UserDetail) {
        val result = UserRepository.unfollow(userDetail.uuid)
        if (result.isSuccess) {
            _profileDetailUiState.update {
                it.copy(
                    userDetail = userDetail.copy(
                        followersCount = userDetail.followersCount - 1,
                        isCurrentUserFollowing = false
                    )
                )
            }
        } else {
            _profileDetailUiState.update {
                it.copy(userMessage = result.exceptionOrNull()!!.message)
            }
        }
    }

    fun userMessageShown() {
        _profileDetailUiState.update {
            it.copy(userMessage = null)
        }
    }
}