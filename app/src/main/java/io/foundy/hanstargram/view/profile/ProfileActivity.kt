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
import kotlinx.coroutines.launch

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding
        get() = ActivityProfileBinding::inflate

    companion object {
        fun getIntent(context: Context, userUuid: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra("userUuid", userUuid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userUuid = intent.getStringExtra("userUuid")!!
        viewModel.bindProfile(userUuid)

        binding.followOrEditButton.setOnClickListener {
            viewModel.toggleFollow()
        }

        /* 유저 디테일 */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileDetailUiState.collect { profileUiState ->
                    if (!profileUiState.isLoading) updateUserDetailUi(profileUiState)
                }
            }
        }

        /* 프로필 포스트 */
        val adapter = ProfilePostAdapter(onClickPost = ::onClickPost)
        initRecyclerView(adapter)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profilePostUiState.collect { postUiState ->
                    updatePostUi(postUiState, adapter)
                }
            }
        }
    }

    /* 프로필 포스트 */
    private fun updatePostUi(uiState: ProfilePostUiState, adapter: ProfilePostAdapter) {
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
        /* Todo: 게시글 자세히 보기
        Snackbar.make(binding.root, uiState.uuid, Snackbar.LENGTH_LONG).show()
         */
    }

    /* 유저 디테일 정보 수정 */
    private fun updateUserDetailUi(uiState: ProfileDetailUiState) {
        uiState.userMessage?.let {
            showSnackBar(it)
            viewModel.userMessageShown()
        }

        uiState.userDetail?.let { userDetail ->
            binding.apply {
                profileHeaderUsernameTextview.text = userDetail.name
                profileIntroduceTextview.text =
                    "${userDetail.name}의 자기소개 입니다." // TODO: 자기소개 추가 시 변경
                profileInfoPostnumTextview.text = userDetail.postCount.toString()
                profileInfoFollowernumTextview.text = userDetail.followersCount.toString()
                profileInfoFolloweenumTextview.text = userDetail.followingCount.toString()

                if (userDetail.isMe) {
                    followOrEditButton.setText(R.string.profile_button_modify)
                    followOrEditButton.setBackgroundColor(getColor(io.foundy.common.R.color.md_theme_light_inversePrimary))
                } else if (userDetail.isCurrentUserFollowing) {
                    followOrEditButton.setText(R.string.profile_button_follow_cancle)
                    followOrEditButton.setBackgroundColor(getColor(io.foundy.common.R.color.md_theme_light_error))
                } else {
                    followOrEditButton.setText(R.string.profile_button_follow)
                    followOrEditButton.setBackgroundColor(getColor(io.foundy.common.R.color.md_theme_light_primary))
                }

                val storageReference = Firebase.storage.reference
                Glide.with(applicationContext)
                    .load(userDetail.profileImageUrl?.let { storageReference.child(it) })
                    .fallback(R.drawable.ic_baseline_person_24)
                    .circleCrop()
                    .into(profileImage)
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}