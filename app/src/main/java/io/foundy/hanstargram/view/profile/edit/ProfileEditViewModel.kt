package io.foundy.hanstargram.view.welcome

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.foundy.data.repository.UserRepository
import io.foundy.hanstargram.view.profile.edit.ProfileEditUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileEditViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileEditUiState> = MutableStateFlow(ProfileEditUiState.None)
    val uiState = _uiState.asStateFlow()

    var isChanged: Boolean = false
    var uuid: String = ""
    var name: String = ""
    var introduce: String = ""
    var selectedImage: Bitmap? = null
    var changedImage: Boolean = false

    val isNamedValid: Boolean
        get() = name.isEmpty()

    fun sendChangedInfo() {
        if(!isChanged) { // 바뀐게 없다면 굳이 변경시킬 필요 없음
            _uiState.update { ProfileEditUiState.SuccessToSave }
            return
        }
        _uiState.update { ProfileEditUiState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            val result : Result<Unit> = UserRepository.updateInfo(name, introduce, selectedImage, changedImage)
            if (result.isSuccess) {
                _uiState.update {
                    ProfileEditUiState.SuccessToSave
                }
            } else {
                _uiState.update { ProfileEditUiState.FailedToSave(result.exceptionOrNull()!!) }
            }
        }
    }
}