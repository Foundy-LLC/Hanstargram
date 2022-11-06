package io.foundy.hanstargram.view.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    val signedIn: Boolean
        get() = AuthRepository.isSignedIn()

    fun signInWith(idToken: String, onComplete: (result: Result<Any>) -> Unit) {
        AuthRepository.signInWith(idToken, onComplete)
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
}