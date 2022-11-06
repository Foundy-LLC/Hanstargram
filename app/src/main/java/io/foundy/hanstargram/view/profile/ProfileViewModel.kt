package io.foundy.hanstargram.view.profile

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import io.foundy.hanstargram.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun getProfileData(uuid : String){
        viewModelScope.launch {
            try{
                val profileUiState = ProfileRepository.getProfileUiState(uuid)
                _uiState.update {
                    it.copy(profileInfo = profileUiState.profileInfo)
                    it.copy(profilePost = profileUiState.profilePost)
                }
            }
            catch (e : Exception){
                _uiState.update {
                    it.copy(message = e.toString())
                }
            }
        }
    }

    fun setImageView(imageView: ImageView, imageSrc : String){
        if(imageSrc == "") return

        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://hanstargram-556db.appspot.com")
        val storageReference = storage.reference
        val pathReference = storageReference.child(imageSrc)
        val glide = Glide.with(imageView.context)

        pathReference.downloadUrl.addOnSuccessListener { uri ->
            glide.load(uri)
                 .diskCacheStrategy(DiskCacheStrategy.NONE)
                 .circleCrop()
                 .into(imageView)
        }
    }
}