package io.foundy.hanstargram.view.posting

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityExamBinding

class ExamActivity : ViewBindingActivity<ActivityExamBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityExamBinding
        get() = ActivityExamBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ExamActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1
        )

        binding.button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(this, PostingActivity::class.java))
            }
        }
    }
}