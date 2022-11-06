package io.foundy.hanstargram.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ActivityLoginBinding
import io.foundy.hanstargram.view.home.HomeActivity
import io.foundy.hanstargram.view.welcome.WelcomeActivity

class LoginActivity : ViewBindingActivity<ActivityLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {

            signUpButton.setOnClickListener {
                navigateToSignUpView()
            }
        }
    }

    private fun onCompleteSignIn(result: Result<Any>) {
        if (result.isSuccess) {
            viewModel.checkUserInfoExists { exists ->
                if (exists) {
                    val sharedPreferences = getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    sharedPreferences.edit()
                        .putBoolean(getString(R.string.prefs_has_user_info), true)
                        .apply()

                    navigateToHomeView()
                } else {
                    navigateToWelcomeView()
                }
            }
        } else {
            showSnackBar(getString(R.string.failed_to_sign_in))
            Log.e(TAG, "Failed firebase sign in: " + result.exceptionOrNull())
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToSignUpView() {
        val intent = SignUpActivity.getIntent(this)
        startActivity(intent)
    }

    private fun navigateToHomeView() {
        val intent = HomeActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToWelcomeView() {
        val intent = WelcomeActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        startActivity(intent)
        finish()
    }
}
