package io.foundy.hanstargramwatch.view.welcome

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.databinding.ActivityWelcomeBinding

class WelcomeActivity: ViewBindingActivity<ActivityWelcomeBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityWelcomeBinding
        get() = ActivityWelcomeBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }



}