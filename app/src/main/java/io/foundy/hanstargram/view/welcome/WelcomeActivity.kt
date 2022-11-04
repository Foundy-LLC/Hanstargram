package io.foundy.hanstargram.view.welcome

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityWelcomeBinding

class WelcomeActivity : ViewBindingActivity<ActivityWelcomeBinding>() {

    private val viewModel: WelcomeViewModel by viewModels()

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { imageUri ->
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

        binding.doneButton.setOnClickListener {
            val name = binding.userNameEditText.text.toString()
            val bitmap = binding.profileImage.drawable?.toBitmap()
            viewModel.sendInfo(name, bitmap)
        }
    }

    private fun showImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }
}