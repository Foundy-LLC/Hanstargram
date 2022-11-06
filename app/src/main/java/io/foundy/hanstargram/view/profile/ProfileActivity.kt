package io.foundy.hanstargram.view.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.GridView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityProfileBinding
import io.foundy.hanstargram.repository.model.PostDto
import kotlinx.coroutines.launch

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {
    private val viewModel : ProfileViewModel by viewModels()
    private val uid = Firebase.auth.currentUser?.uid.toString()

    private lateinit var gridView : GridView

    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding
        get() = ActivityProfileBinding::inflate

    companion object {
        private const val TAG = "ProfileActivity"
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridView = binding.profilePostGrid

        val intent = Companion.getIntent(this)
        intent.getStringExtra("uuid")?.let {
            viewModel.getProfileData(it)
            Log.d(TAG, it + "입니다")
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { uiState ->
                    binding.apply {
                        profileHeaderUsernameTextview.text = uiState.profileInfo.name
                        profileIntroduceTextview.text = uiState.toString()
                        profileInfoFollowernumTextview.text = uiState.profileInfo.follower.toString()
                        profileInfoFollowingnumTextview.text = uiState.profileInfo.followee.toString()
                        profileInfoPostnumTextview.text = uiState.profileInfo.post.toString()
                        if(uiState.profileInfo.isMine){
                            profileModifyButton.text = "프로필 수정"
                        }
                        else{
                            profileModifyButton.text = "팔로우"
                        }
                    }
                    viewModel.setImageView(binding.profileImage, uiState.profileInfo.profileImg)
                    gridView.adapter = ProfilePostAdapter(applicationContext, uiState.profilePost)
                }
            }
        }
    }
}