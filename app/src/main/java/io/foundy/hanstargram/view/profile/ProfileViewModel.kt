package io.foundy.hanstargram.view.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.data.repository.ProfileRepository
import io.foundy.hanstargram.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ProfileState {
    IsMyProfile,
    Following,
    UnFollowing
}

class ProfileViewModel : ViewModel() {
    private lateinit var thisUuid : String
    private lateinit var curUuid : String
    private lateinit var profileState : ProfileState
    private val TAG = "ProfileViewModel"

    private val _postUiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(PostUiState())
    val postUiState = _postUiState.asStateFlow()

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun init(uuid : String){
        this.thisUuid = uuid
        this.curUuid = Firebase.auth.currentUser?.uid.toString()

        checkFollowing()
        getProfileData()
        getPostCount()
        getFollowerCount()
        getFolloweeCount()
    }

    private fun checkFollowing(){
        var state : Int

        if(thisUuid == curUuid){ // 내 프로필이면 그냥 state만 변경하고 리턴
            profileState = ProfileState.IsMyProfile
            state = 0
            _uiState.update {
                it.copy(state = state)
            }
        }
        else{
            viewModelScope.launch { // 일단 다른 유저라면 팔로우 중인치 체크
                try{
                    if(ProfileRepository.isFollowThisUser(thisUuid)){ // 팔로우 중이라면 팔로우 취소가 떠야 함
                        profileState = ProfileState.Following
                        state = 1
                    } else{ // 팔로우를 안하고 있다면 팔로우가 떠야 함
                        profileState = ProfileState.UnFollowing
                        state = 2
                    }
                    _uiState.update { // 아무튼 state를 업데이트
                        it.copy(state = state)
                    }
                }
                catch (e: Exception){
                    _uiState.update {
                        it.copy(userMessage = R.string.profile_error_temp)
                    }
                }
            }
        }
    }

    fun getProfileData(){
        viewModelScope.launch {
            try{
                val userDto = ProfileRepository.getUserDto(thisUuid)
                _uiState.update {
                    it.copy(userInfo = userDto)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(userMessage = R.string.profile_error_temp)
                }
            }
        }
    }

    fun getPostCount(){
        viewModelScope.launch {
            try{
                val postCount = ProfileRepository.getPostCount(thisUuid).toInt()
                _uiState.update {
                    it.copy(post = postCount)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(userMessage = R.string.profile_error_temp)
                }
            }
        }
    }

    fun getFollowerCount(){
        viewModelScope.launch {
            try{
                val followerCount = ProfileRepository.getFollowerCount(thisUuid).toInt()
                _uiState.update {
                    it.copy(follower = followerCount)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(userMessage = R.string.profile_error_temp)
                }
            }
        }
    }

    fun getFolloweeCount(){
        viewModelScope.launch {
            try{
                val followeeCount = ProfileRepository.getFolloweeCount(thisUuid).toInt()
                _uiState.update {
                    it.copy(followee = followeeCount)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(userMessage = R.string.profile_error_temp)
                }
            }
        }
    }

    fun actionProfileButton(){
        if(profileState == ProfileState.IsMyProfile){
            // TODO: 프로필 수정 액티비티로 이동 ㄱㄱ
        }
        else if (profileState == ProfileState.Following){ // 팔로우 중일 때 실행, 팔로우 취소 액션
            profileState = ProfileState.UnFollowing
            viewModelScope.launch {
                if(ProfileRepository.unFollow(thisUuid).isSuccess){
                    val followerCount = ProfileRepository.getFollowerCount(thisUuid).toInt()
                    _uiState.update {
                        it.copy(follower = followerCount, state = 2, userMessage = R.string.profile_follow_cancle)
                    }
                }
                else{
                    _uiState.update {
                        it.copy(userMessage = R.string.profile_error_temp)
                    }
                }
            }
        }
        else if (profileState == ProfileState.UnFollowing){ // 팔로우 중이 아닐 때 실행, 팔로우 하는 액션
            profileState = ProfileState.Following
            viewModelScope.launch {
                if(ProfileRepository.doFollow(thisUuid).isSuccess){
                    val followerCount = ProfileRepository.getFollowerCount(thisUuid).toInt()
                    _uiState.update {
                        it.copy(follower = followerCount, state = 1, userMessage = R.string.profile_follow_success)
                    }
                }
                else{
                    _uiState.update {
                        it.copy(userMessage = R.string.profile_error_temp)
                    }
                }
            }
        }
        else{
            _uiState.update {
                it.copy(userMessage = R.string.profile_error_temp)
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

//    fun setImageView(imageView: ImageView, imageSrc : String){
//        if(imageSrc == "") return
//
//        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://hanstargram-556db.appspot.com")
//        val storageReference = storage.reference
//        val pathReference = storageReference.child(imageSrc)
//        val glide = Glide.with(imageView.context)
//
//        pathReference.downloadUrl.addOnSuccessListener { uri ->
//            glide.load(uri)
//                 .diskCacheStrategy(DiskCacheStrategy.NONE)
//                 .circleCrop()
//                 .into(imageView)
//        }
//    }
}