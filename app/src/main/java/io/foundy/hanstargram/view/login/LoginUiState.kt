package io.foundy.hanstargram.view.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val successToSignIn: Boolean = false,
    val userMessage: String? = null
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