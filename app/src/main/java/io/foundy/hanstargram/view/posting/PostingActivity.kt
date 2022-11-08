package io.foundy.hanstargram.view.posting

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import io.foundy.hanstargram.R
import kotlinx.coroutines.launch

class PostingActivity : AppCompatActivity() {

    private val viewModel: PostingViewModel by viewModels()

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
                viewModel.selectImage(bitmap)
            }
        }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PostingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        showImagePicker()

        val postButton = findViewById<ImageButton>(R.id.post_button)

        postButton.setOnClickListener {
            val content = findViewById<EditText>(R.id.image_expression).text.toString()
            viewModel.uploadContent(content)
        }

        val imageView = findViewById<ImageView>(R.id.add_image)
        imageView.setOnClickListener {
            showImagePicker()
        }

        val backButton = findViewById<ImageButton>(R.id.post_back_button)
        backButton.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::updateUi)
            }
        }
    }

    private fun updateUi(uiState: PostingUiState) {
        if (uiState.selectedImage != null) {
            findViewById<ImageView>(R.id.add_image).setImageBitmap(uiState.selectedImage)
        }
        if (uiState.userMessage != null) {
            showSnackBar(getString(uiState.userMessage))
            viewModel.userMessageShown()
        }
        if (uiState.successToUpload) {
            Toast.makeText(this, "게시글 업로드에 성공했습니다.", Toast.LENGTH_LONG).show()
            finish()
        }

        findViewById<ImageButton>(R.id.post_button).apply {
            isEnabled = !uiState.isLoading
            alpha = if (uiState.isLoading) 0.5F else 1.0F
        }
    }

    private fun showImagePicker() {
        if (!viewModel.uiState.value.isLoading) {
            fileChooserContract.launch("image/*")

        }
    }

    private fun showSnackBar(message: String) {
        val root = findViewById<ConstraintLayout>(R.id.posting_root)
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
    }
}