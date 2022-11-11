package io.foundy.hanstargram.view.welcome

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import io.foundy.hanstargram.view.profile.edit.ProfileEditUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileEditViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileEditUiState> =
        MutableStateFlow(ProfileEditUiState.None)
    val uiState = _uiState.asStateFlow()

    var name: String = ""
    var introduce: String = ""
    var selectedImage: Bitmap? = null

//    fun sendInfo() {
//        _uiState.update { WelcomeUiState.Loading }
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = UserRepository.saveInitUserInfo(name, selectedImage)
//            if (result.isSuccess) {
//                _uiState.update { WelcomeUiState.SuccessToSave }
//            } else {
//                _uiState.update { WelcomeUiState.FailedToSave(result.exceptionOrNull()!!) }
//            }
//        }
//    }
}