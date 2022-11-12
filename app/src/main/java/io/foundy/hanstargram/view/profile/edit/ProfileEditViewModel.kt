package io.foundy.hanstargram.view.profile.edit

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.foundy.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileEditViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState = _uiState.asStateFlow()

    private var didBound = false
    private lateinit var oldName: String
    private lateinit var oldIntroduce: String

    val isChanged
        get() = uiState.value.isImageChanged ||
                oldName != uiState.value.name ||
                oldIntroduce != uiState.value.introduce

    val canSave: Boolean
        get() = uiState.value.name.isNotEmpty() && !uiState.value.isLoading && isChanged

    fun bind(oldName: String, oldIntroduce: String) {
        check(!didBound)
        didBound = true
        this.oldName = oldName
        this.oldIntroduce = oldIntroduce
        _uiState.update {
            it.copy(name = oldName, introduce = oldIntroduce)
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateIntroduce(introduce: String) {
        _uiState.update { it.copy(introduce = introduce) }
    }

    fun updateImageBitmap(bitmap: Bitmap?) {
        _uiState.update { it.copy(selectedImageBitmap = bitmap, isImageChanged = true) }
    }

    fun sendChangedInfo() {
        if (!isChanged) {
            _uiState.update { it.copy(successToSave = true) }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val uiStateValue = uiState.value
            val result = UserRepository.updateInfo(
                name = uiStateValue.name,
                introduce = uiStateValue.introduce,
                profileImage = uiStateValue.selectedImageBitmap,
                isChangedImage = uiStateValue.isImageChanged
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(successToSave = true, isLoading = false)
                }
            } else {
                _uiState.update {
                    it.copy(
                        userMessage = result.exceptionOrNull()!!.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}