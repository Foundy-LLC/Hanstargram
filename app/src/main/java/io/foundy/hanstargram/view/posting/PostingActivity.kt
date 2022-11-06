package io.foundy.hanstargram.view.posting

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ActivityPostingBinding
import java.util.*

class PostingActivity : ViewBindingActivity<ActivityPostingBinding>() {
    private val viewModel: PostingViewModel by viewModels()

    private var storage: FirebaseStorage? = null
    private var photoUri: Uri? = null
    private var auth: FirebaseAuth? = null

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
                photoUri = imageUri
                val imgView = findViewById<ImageView>(R.id.add_image)
                imgView.setImageBitmap(bitmap)
            }
        }

    override val bindingInflater: (LayoutInflater) -> ActivityPostingBinding
        get() = ActivityPostingBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PostingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        showImagePicker()

        val postButton = findViewById<Button>(R.id.post_button)

        postButton.setOnClickListener {
            contentUpload()
        }
    }

    private fun showImagePicker() {
        fileChooserContract.launch("image/*")
    }


    private fun contentUpload() {
        val imageFileName: String = UUID.randomUUID().toString() + ".png"

        // Create a storage reference from our app
        val storageRef = storage?.reference

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef?.child(imageFileName)

        mountainsRef?.putFile(photoUri!!)?.addOnSuccessListener {
            val content = findViewById<EditText>(R.id.image_expression).text.toString()
            val imageUrl = photoUri.toString()
            viewModel.uploadContent(content, imageUrl)
            Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
        }?.addOnFailureListener {
            Toast.makeText(this, "업로드 실패", Toast.LENGTH_LONG).show()
        }
    }
}