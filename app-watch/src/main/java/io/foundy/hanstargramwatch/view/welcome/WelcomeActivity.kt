package io.foundy.hanstargramwatch.view.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.R
import io.foundy.hanstargramwatch.databinding.ActivityWelcomeBinding
import io.foundy.hanstargramwatch.view.home.HomeActivity
import kotlinx.coroutines.launch

class WelcomeActivity : ViewBindingActivity<ActivityWelcomeBinding>() {

    private val viewModel: WelcomeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityWelcomeBinding
        get() = ActivityWelcomeBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.userNameEditText.setText(viewModel.name)

        binding.userNameEditText.addTextChangedListener {
            if (it != null) {
                viewModel.name = it.toString()
                updateDoneButton()
            }
        }

        binding.doneButton.setOnClickListener {
            viewModel.sendInfo()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::updateUi)
            }
        }
    }

    private fun updateUi(uiState: WelcomeUiState) {
        updateDoneButton()
        when (uiState) {
            WelcomeUiState.SuccessToSave -> {
                val sharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                sharedPreferences.edit()
                    .putBoolean(getString(R.string.prefs_has_user_info), true)
                    .apply()

                navigateToHomeView()
            }
            is WelcomeUiState.FailedToSave -> {
                showSnackBar(getString(R.string.failed_to_save_data))
            }
            else -> {}
        }
    }

    private fun updateDoneButton() {
        val isLoading = viewModel.uiState.value is WelcomeUiState.Loading
        val hasName = binding.userNameEditText.text.toString().isNotEmpty()

        binding.doneButton.apply {
            isEnabled = hasName && !isLoading
            text = getString(if (isLoading) R.string.loading else R.string.done)
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToHomeView() {
        val intent = HomeActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        startActivity(intent)
        finish()
    }
}