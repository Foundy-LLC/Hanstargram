package io.foundy.hanstargram.view.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private lateinit var uuid : String
    private lateinit var curuuid : String
    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun init(uuid : String){
        this.uuid = uuid
        this.curuuid = Firebase.auth.currentUser?.uid.toString()
        getProfileData()
        getPostCount()
        getFollowerCount()
        getFolloweeCount()
    }

    fun getProfileData(){
        viewModelScope.launch {
            try{
                val userDto = ProfileRepository.getUserDto(uuid)
                _uiState.update {
                    it.copy(userInfo = userDto, isMine = (userDto.uuid == curuuid))
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(message = e.toString())
                }
            }
        }
    }

    fun getPostCount(){
        viewModelScope.launch {
            try{
                val postCount = ProfileRepository.getPostCount(uuid).toInt()
                _uiState.update {
                    it.copy(post = postCount)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(message = e.toString())
                }
            }
        }
    }

    fun getFollowerCount(){
        viewModelScope.launch {
            try{
                val followerCount = ProfileRepository.getFollowerCount(uuid).toInt()
                _uiState.update {
                    it.copy(follower = followerCount)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(message = e.toString())
                }
            }
        }
    }

    fun getFolloweeCount(){
        viewModelScope.launch {
            try{
                val followeeCount = ProfileRepository.getFolloweeCount(uuid).toInt()
                _uiState.update {
                    it.copy(followee = followeeCount)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(message = e.toString())
                }
            }
        }
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