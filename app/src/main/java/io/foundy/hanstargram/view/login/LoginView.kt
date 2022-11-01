package io.foundy.hanstargram.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityLoginBinding

class LoginView : ViewBindingActivity<ActivityLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO(민성): 이미 로그인 된 경우 홈 화면으로 전환하기

        initSignInButton()
    }

    private fun initSignInButton() {
        binding.signInButton.apply {
            val textView = getChildAt(0) as? TextView
            textView?.let { it.text = context.getString(R.string.sign_in_with_google) }
            setOnClickListener { signIn() }
        }
    }

    private fun signIn() {
        // TODO(민성): 구현하기
    }
}