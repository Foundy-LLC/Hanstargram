package io.foundy.hanstargram.view.welcome

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityWelcomeBinding

class WelcomeActivity : ViewBindingActivity<ActivityWelcomeBinding>() {

    private val fileChooserContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            if (imageUri != null) {
                @Suppress("DEPRECATION")
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            contentResolver,
                            imageUri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                binding.profileImage.setImageBitmap(bitmap)
            }
        }

    override val bindingInflater: (LayoutInflater) -> ActivityWelcomeBinding
        get() = ActivityWelcomeBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.profileImage.setOnClickListener {
            showImagePicker()
        }

        binding.userNameEditText.addTextChangedListener {
            if (it != null) {
                binding.doneButton.isEnabled = it.isNotEmpty()
            }
        }
    }

    private fun showImagePicker() {
        fileChooserContract.launch("image/*")
    }
}