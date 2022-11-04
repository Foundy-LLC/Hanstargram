package io.foundy.hanstargram.view.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityProfileBinding
import io.foundy.hanstargram.view.profile.profilepostlist.ProfilePostAdapter
import io.foundy.hanstargram.view.profile.profilepostlist.ProfilePostItem

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()
    private var gridView:GridView ?= null
    private var arrayList:ArrayList<ProfilePostItem> ?= null
    private var profilePostAdapter:ProfilePostAdapter ?= null

    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore


    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding
        get() = ActivityProfileBinding::inflate

    companion object {
        private const val TAG = "ProfileActivity"

        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gridView = findViewById(R.id.profile_post_grid)
        arrayList = setDataList()
        profilePostAdapter = ProfilePostAdapter(applicationContext, arrayList!!)
        gridView?.adapter = profilePostAdapter

        if (Firebase.auth.currentUser == null) {
            Log.d(TAG, "로그인 에러")
        }

        var userId : String = Firebase.auth.currentUser?.uid ?: "Can't Read"
        var userProfileSrc : String = "0c8b55d6ef764c46bd0615661f9bf2bf.png"

        db = Firebase.firestore
        db.collection("users").document(userId).get().addOnSuccessListener {
            userId = it["name"].toString()
            userProfileSrc = it["profileImageUrl"].toString()
            binding.profileHeaderUsernameTextview.text = userId
            binding.profileIntroduceTextview.text = "${it["name"]}, ${it["profileImageUrl"]}"
        }

        storage = Firebase.storage
        val storageRef = storage.reference
        //val profileRef = storage.getReferenceFromUrl("gs://hanstargram-556db.appspot.com/0c8b55d6ef764c46bd0615661f9bf2bf.png")
        val profileRef = storageRef.child(userProfileSrc)
        displayImageRef(profileRef, binding.profileImage)
    }

    private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
            Log.e(TAG, "프로필 이미지 가져오기 에러")
            Log.e(TAG, imageRef.toString())
        }
    }

    private fun setDataList() : ArrayList<ProfilePostItem>{
        val arrayList : ArrayList<ProfilePostItem> = ArrayList()
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        return arrayList
    }
}