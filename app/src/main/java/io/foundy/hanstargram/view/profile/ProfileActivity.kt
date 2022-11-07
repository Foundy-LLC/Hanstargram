package io.foundy.hanstargram.view.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ActivityProfileBinding
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.setListeners
import io.foundy.hanstargram.view.home.search.ProfilePostAdapter
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
        viewModel.init(intent.getStringExtra("uuid")!!)

        binding.profileButton.setOnClickListener {
            viewModel.actionProfileButton()
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { uiState ->
                    updateUi(uiState)
                }
            }
        }

        val adapter = ProfilePostAdapter(onClickPost = ::onClickPost)
        initRecyclerView(adapter)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profilePostUiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    private fun updateUi(uiState: ProfilePostUiState, adapter: ProfilePostAdapter) {
        adapter.submitData(lifecycle, uiState.pagingData)
    }

    private fun initRecyclerView(adapter: ProfilePostAdapter) {
        binding.apply {
            recyclerView.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            recyclerView.layoutManager =
                GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

            loadState.setListeners(adapter, swipeRefreshLayout)
        }
    }

    private fun onClickPost(uiState: ProfilePostItemUiState) {
        Snackbar.make(binding.root, uiState.uuid, Snackbar.LENGTH_LONG).show()
    }

    private fun showSnackBar(message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun updateUi(uiState: ProfileUiState) {
        val userInfo = uiState.userInfo

        if (uiState.userMessage != null) {
            showSnackBar(uiState.userMessage)
            viewModel.userMessageShown()
        }

        binding.apply {
            profileHeaderUsernameTextview.text = userInfo.name
            profileIntroduceTextview.text = "${userInfo.name}의 자기소개 입니다." // ToDo: 자기소개 추가 시 변경
            profileInfoPostnumTextview.text = uiState.post.toString()
            profileInfoFollowernumTextview.text = uiState.follower.toString()
            profileInfoFolloweenumTextview.text = uiState.followee.toString()

            val profileState = uiState.state
            if(profileState == 0){
                profileButton.setText(R.string.profile_button_modify)
            }
            else if (profileState == 1){
                profileButton.setText(R.string.profile_button_follow_cancle)
            }
            else if (profileState == 2){
                profileButton.setText(R.string.profile_button_follow)
            }

            val storageReference = Firebase.storage.reference
            Glide.with(applicationContext)
                .load(userInfo.profileImageUrl?.let { storageReference.child(it) })
                .circleCrop()
                .into(profileImage)
        }

        //viewModel.setImageView(binding.profileImage, uiState.profileInfo.profileImg)
        //gridView.adapter = ProfilePostAdapter(applicationContext, uiState.profilePost)
    }
}