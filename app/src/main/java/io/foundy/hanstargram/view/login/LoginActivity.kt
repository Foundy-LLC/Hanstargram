package io.foundy.hanstargram.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityLoginBinding
import io.foundy.hanstargram.view.home.HomeActivity

class LoginActivity : ViewBindingActivity<ActivityLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.signedIn) {
            navigateToHomeView()
        }

        initSignInLauncher()
        initGoogleSignInClient()
        initSignInButton()
    }

    private fun initSignInLauncher() {
        val contracts = ActivityResultContracts.StartActivityForResult()
        signInLauncher = registerForActivityResult(contracts) {
            if (it.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.signInWith(account.idToken!!, ::onCompleteSignIn)
                    Log.d(TAG, "Success google sign in: " + account.id)
                } catch (e: ApiException) {
                    showSnackBar(getString(R.string.failed_to_sign_in))
                    Log.e(TAG, "Failed google sign in: " + e.message)
                }
            } else {
                val resultCode = it.resultCode
                showSnackBar(getString(R.string.failed_to_sign_in))
                Log.e(TAG, "Failed google sign in code: $resultCode")
            }
        }
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun initSignInButton() {
        binding.signInButton.apply {
            val textView = getChildAt(0) as? TextView
            textView?.let { it.text = context.getString(R.string.sign_in_with_google) }
            setOnClickListener { signIn() }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun onCompleteSignIn(result: Result<Any>) {
        if (result.isSuccess) {
            navigateToHomeView()
        } else {
            showSnackBar(getString(R.string.failed_to_sign_in))
            Log.e(TAG, "Failed firebase sign in: " + result.exceptionOrNull())
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToHomeView() {
        val intent = HomeActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        startActivity(intent)
        finish()
    }
}
