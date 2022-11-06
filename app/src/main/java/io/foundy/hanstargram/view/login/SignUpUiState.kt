package io.foundy.hanstargram.view.login

import androidx.annotation.StringRes

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val successToSignUp: Boolean = false,
    @StringRes
    val userMessage: Int? = null
) {
    val isInputValid: Boolean
        get() = isEmailValid && isPasswordValid

    private val isEmailValid: Boolean
        get() {
            return if (email.isEmpty()) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }

    private val isPasswordValid: Boolean
        get() = password.length >= 6

    val showEmailError: Boolean
        get() = email.isNotEmpty() && !isEmailValid

    val showPasswordError: Boolean
        get() = password.isNotEmpty() && !isPasswordValid
}