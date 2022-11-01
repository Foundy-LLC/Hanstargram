package io.foundy.hanstargram.view.login

import androidx.lifecycle.ViewModel
import io.foundy.hanstargram.repository.AuthRepository

class LoginViewModel : ViewModel() {

    val signedIn: Boolean
        get() = AuthRepository.isSignedIn()

    fun signInWith(idToken: String, onComplete: (result: Result<Any>) -> Unit) {
        AuthRepository.signInWith(idToken, onComplete)
    }
}