package io.foundy.hanstargram.view.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {
    private val viewModel : ProfileViewModel by viewModels()

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

        // uuid를 viewmodel에 등록하여 초기화 실행
        viewModel.init(intent.getStringExtra("uuid") ?: "설마에러")

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { uiState ->
                    updateUi(uiState)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(uiState: ProfileUiState) {
        binding.apply {
            val userInfo = uiState.userInfo

            profileHeaderUsernameTextview.text = userInfo.name
            profileIntroduceTextview.text = "${userInfo.name}의 자기소개 입니다."

            profileInfoPostnumTextview.text = uiState.post.toString()
            profileInfoFollowernumTextview.text = uiState.followee.toString()
            profileInfoFolloweenumTextview.text = uiState.followee.toString()

            Log.d(TAG, uiState.isMine.toString())

            if(uiState.isMine){
                profileModifyButton.text = "프로필 수정"
            }
            else{
                profileModifyButton.text = "팔로우"
            }
        }
        //viewModel.setImageView(binding.profileImage, uiState.profileInfo.profileImg)
        //gridView.adapter = ProfilePostAdapter(applicationContext, uiState.profilePost)
    }
}