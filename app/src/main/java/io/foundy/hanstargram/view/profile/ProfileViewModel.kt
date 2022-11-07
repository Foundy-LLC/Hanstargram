package io.foundy.hanstargram.view.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.data.repository.PostRepository
import io.foundy.data.repository.UserRepository
import io.foundy.hanstargram.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private lateinit var targetUuid : String
    private var currentUuid : String

    private val TAG = "ProfileViewModel"

    init {
        this.currentUuid = Firebase.auth.currentUser?.uid.toString()
    }

    fun bindProfile(targetUuid : String){
        this.targetUuid = targetUuid
        searchPostByUuid()
        getProfileDetail()
        getIsFollowing()
    }

    /* Post Ui State 구역 */
    private val _profilePostUiState = MutableStateFlow(ProfilePostUiState())
    val profilePostUiState = _profilePostUiState.asStateFlow()

    private fun searchPostByUuid() {
        viewModelScope.launch {
            PostRepository.getPostsByUuid(targetUuid).cachedIn(viewModelScope).collectLatest { pagingData ->
                _profilePostUiState.update { profilePostUiState ->
                    profilePostUiState.copy(pagingData = pagingData.map { it.toProfilePostItemUiState() })
                }
            }
        }
    }

    /* UserDetail Ui State 구역 */
    private val _profileDetailUiState: MutableStateFlow<ProfileDetailUiState> = MutableStateFlow(ProfileDetailUiState())
    val profileDetailUiState = _profileDetailUiState.asStateFlow()

    private fun getProfileDetail(){
        _profileDetailUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            UserRepository.getUserDetail(targetUuid)
                .onSuccess { userDetails ->
                    _profileDetailUiState.update {
                        it.copy(userDetails = userDetails, isLoading = false)
                    }
                }
                .onFailure { e->
                    _profileDetailUiState.update {
                        it.copy(userMessage = e.localizedMessage?.toInt(), isLoading = false)
                    }
                }
        }
    }

    private fun getIsFollowing() {
        _profileDetailUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch { // 일단 다른 유저라면 팔로우 중인치 체크
            UserRepository.getIsFollowing(targetUuid)
                .onSuccess { value ->
                    _profileDetailUiState.update {
                        it.copy(isFollowing = value, isLoading = false)
                    }
                }
                .onFailure { e ->
                    _profileDetailUiState.update {
                        it.copy(userMessage = e.localizedMessage?.toInt(), isLoading = false)
                    }
                }
        }
    }

    fun actionProfileButton() {
        _profileDetailUiState.update { it.copy(isLoading = true) }

        if(targetUuid == currentUuid){ // 프로필 수정
            _profileDetailUiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch { // 일단 다른 유저라면 팔로우 중인치 체크
            UserRepository.getIsFollowing(targetUuid)
                .onSuccess { isFollowing ->
                    if (isFollowing) { // 팔로우 하고 있다면 팔로우를 해제 했음.
                        UserRepository.unfollow(targetUuid)
                            .onSuccess {
                                _profileDetailUiState.update {
                                    it.copy(
                                        userMessage = R.string.profile_follow_cancle,
                                        isFollowing = false,
                                        isLoading = false
                                    )
                                }
                            }
                            .onFailure {
                                _profileDetailUiState.update {
                                    it.copy(
                                        userMessage = R.string.profile_error_temp,
                                        isLoading = false
                                    )
                                }
                            }
                    } else { // 팔로우 하고 있지 않다면 팔로우를 신청 했음.
                        UserRepository.follow(targetUuid)
                            .onSuccess {
                                _profileDetailUiState.update {
                                    it.copy(
                                        userMessage = R.string.profile_follow_success,
                                        isFollowing = true,
                                        isLoading = false
                                    )
                                }
                            }
                            .onFailure {
                                _profileDetailUiState.update {
                                    it.copy(
                                        userMessage = R.string.profile_error_temp,
                                        isLoading = false
                                    )
                                }
                            }
                    }
                }
                .onFailure { e ->
                    _profileDetailUiState.update {
                        it.copy(
                            userMessage = e.localizedMessage?.toInt(),
                            isLoading = false
                        )
                    }
                }
            // 끝나고 유저 디테일 한번 업데이트
            getProfileDetail()
        }
    }

    fun userMessageShown() {
        _profileDetailUiState.update {
            it.copy(userMessage = null)
        }
    }
}