package io.foundy.hanstargram.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    val signedIn: Boolean
        get() = AuthRepository.isSignedIn()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun signIn() {
        val email = uiState.value.email
        val password = uiState.value.password
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = AuthRepository.signIn(email, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(successToSignIn = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        userMessage = result.exceptionOrNull()!!.localizedMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun checkUserInfoExists(hasUserInfoCallback: (Boolean) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            hasUserInfoCallback(false)
            return
        }
        AuthRepository.hasUserInfo(uid) {
            hasUserInfoCallback(it)
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}