package io.foundy.hanstargramwatch.view.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.databinding.ActivityHomeBinding

class HomeActivity : ViewBindingActivity<ActivityHomeBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

}