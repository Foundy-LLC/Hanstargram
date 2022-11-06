package io.foundy.hanstargram.view.posting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.storage.FirebaseStorage
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityPostingBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class PostingActivity : ViewBindingActivity<ActivityPostingBinding>() {

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
                binding.addImage.setImageBitmap(bitmap)
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

        showImagePicker()

        binding.postButton.setOnClickListener {
            //contentUpload()
        }
    }

    private fun showImagePicker() {
        fileChooserContract.launch("image/*")
    }


//    private fun contentUpload() {
//        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var imageFileName = "IMAGE_" + timestamp + "_.png"
//
//        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
//
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//            Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
//        }
//    }
}