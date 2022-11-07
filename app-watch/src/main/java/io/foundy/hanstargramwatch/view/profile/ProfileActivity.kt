package io.foundy.hanstargramwatch.view.profile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.databinding.ActivityProfileBinding

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding
        get() = ActivityProfileBinding::inflate

    companion object {
        fun getIntent(context: Context, userUuid: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra("userUuid", userUuid)
            }
        }
    }
}