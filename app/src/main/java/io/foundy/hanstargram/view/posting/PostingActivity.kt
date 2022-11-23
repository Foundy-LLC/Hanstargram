package io.foundy.hanstargram.view.posting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.R
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class PostingActivity : AppCompatActivity() {

    private val viewModel: PostingViewModel by viewModels()
    private val storageReference = Firebase.storage.reference


    private val fileChooserContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->

            if (imageUri != null) {
                if (imageUri.toFile().length() < 10000000) {//checkImageSize(imageUri)
                    viewModel.selectImage(imageUri)
                }
            } else if (viewModel.uiState.value.selectedImage == null && viewModel.uiState.value.isCreating) {
                finish()
            }
        }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PostingActivity::class.java)
        }

        fun getIntent(
            context: Context,
            postContent: String,
            postImage: String,
            postUuid: String
        ): Intent {
            return Intent(context, PostingActivity::class.java)
                .putExtra("content", postContent)
                .putExtra("image", postImage)
                .putExtra("uuid", postUuid)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        val glide = Glide.with(this)
        val contentEditText = findViewById<EditText>(R.id.image_expression)
        val imageView = findViewById<ImageView>(R.id.add_image)
        val backButton = findViewById<ImageButton>(R.id.post_back_button)
        val postButton = findViewById<ImageButton>(R.id.post_button)
        val postContent = intent.getStringExtra("content")
        val postImage = intent.getStringExtra("image")
        val postUuid = intent.getStringExtra("uuid")

        if (postContent != null && postImage != null && postUuid != null) {
            viewModel.changeToEditMode()
            glide.load(storageReference.child(postImage))
                .into(imageView)

            contentEditText.setText(postContent)
        } else {
            showImagePicker()
        }

        postButton.setOnClickListener {
            if (imageView.drawable == null) {
                showSnackBar(getString(R.string.select_image))
            } else {
                if (!viewModel.uiState.value.isCreating) {
                    viewModel.editContent(postUuid.toString(), contentEditText.text.toString())
                } else {
                    viewModel.uploadContent(contentEditText.text.toString())
                }
            }
        }

        imageView.setOnClickListener {
            showImagePicker()
        }

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
            findViewById<ImageView>(R.id.add_image).setImageURI(uiState.selectedImage)
        }
        if (uiState.userMessage != null) {
            showSnackBar(getString(uiState.userMessage))
            viewModel.userMessageShown()
        }
        if (uiState.successToUpload) {
            Toast.makeText(this, "게시글 업로드에 성공했습니다.", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
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

    @Suppress("DEPRECATION")
    private fun checkImageSize(imageUri: Uri): Boolean {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageInByte = stream.toByteArray()
        val size = imageInByte.size

        return if (size > 10000000) {
            println("size : $size")
            showSnackBar(getString(R.string.image_oversized))
            false
        } else {
            println("size : $size")
            true
        }
    }

    private fun showSnackBar(message: String) {
        val root = findViewById<ConstraintLayout>(R.id.posting_root)
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
    }
}

