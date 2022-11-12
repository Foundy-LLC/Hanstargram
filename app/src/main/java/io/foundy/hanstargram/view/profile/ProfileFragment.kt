package io.foundy.hanstargram.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.common.base.ViewBindingFragment
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.FragmentProfileBinding
import io.foundy.hanstargram.util.themeColor
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.setListeners
import io.foundy.hanstargram.view.home.postlist.PostItemUiState
import io.foundy.hanstargram.view.profile.edit.ProfileEditFragment
import kotlinx.coroutines.launch

class ProfileFragment(
    private val onClickPost: (uiState: PostItemUiState) -> Unit
) : ViewBindingFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("ProfileEdit") { _, bundle ->
            if(bundle.getBoolean("isChanged")){
                val uuid = bundle.getString("uuid")
                if (uuid != null) {
                    viewModel.getProfileDetail(uuid)
                }
            }
        }

        initToolbar()

        binding.followOrEditButton.setOnClickListener {
            val isMe = viewModel.profileDetailUiState.value.userDetail!!.isMe
            if (isMe) {
                changeToProfileEditFragment()
            } else {
                viewModel.toggleFollow()
            }
        }

        /* 유저 디테일 */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileDetailUiState.collect { profileUiState ->
                    if (!profileUiState.isLoading) updateUserDetailUi(profileUiState)
                }
            }
        }

        /* 프로필 포스트 */
        val adapter = ProfilePostAdapter(onClickPost = onClickPost)
        initRecyclerView(adapter)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profilePostUiState.collect { postUiState ->
                    updatePostUi(postUiState, adapter)
                }
            }
        }
    }

    private fun initToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolBar)
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }
    }

    /* 프로필 수정 프래그먼트*/
    private fun changeToProfileEditFragment() {
        val fragmentManager = parentFragmentManager
        val profileEditFragment = ProfileEditFragment(viewModel.profileDetailUiState.value.userDetail!!)
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view, profileEditFragment, "ProfileEdit")
            addToBackStack(null)
        }.commit()
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
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

            loadState.setListeners(adapter, swipeRefreshLayout)
        }
    }

    /* 유저 디테일 정보 수정 */
    private fun updateUserDetailUi(uiState: ProfileDetailUiState) {
        uiState.userMessage?.let {
            showSnackBar(it)
            viewModel.userMessageShown()
        }

        uiState.userDetail?.let { userDetail ->
            binding.apply {
                val colorGrey = ColorUtils.setAlphaComponent(
                    themeColor(com.google.android.material.R.attr.colorOnBackground),
                    20
                )
                val colorOnBackground = themeColor(
                    com.google.android.material.R.attr.colorOnBackground
                )
                val colorPrimary = themeColor(androidx.appcompat.R.attr.colorPrimary)
                val colorOnPrimary = themeColor(
                    com.google.android.material.R.attr.colorOnPrimary
                )
                profileHeaderUsernameTextview.text = userDetail.name
                profileIntroduceTextview.text = userDetail.introduce
                profileInfoPostnumTextview.text = userDetail.postCount.toString()
                profileInfoFollowernumTextview.text = userDetail.followersCount.toString()
                profileInfoFolloweenumTextview.text = userDetail.followingCount.toString()

                followOrEditButton.isVisible = true
                if (userDetail.isMe) {
                    followOrEditButton.setText(R.string.profile_button_modify)
                    followOrEditButton.setTextColor(colorOnBackground)
                    followOrEditButton.setBackgroundColor(colorGrey)
                } else if (userDetail.isCurrentUserFollowing) {
                    followOrEditButton.setText(R.string.profile_button_follow_cancle)
                    followOrEditButton.setTextColor(colorOnBackground)
                    followOrEditButton.setBackgroundColor(colorGrey)
                } else {
                    followOrEditButton.setText(R.string.profile_button_follow)
                    followOrEditButton.setTextColor(colorOnPrimary)
                    followOrEditButton.setBackgroundColor(colorPrimary)
                }

                val storageReference = Firebase.storage.reference
                Glide.with(this@ProfileFragment)
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

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
}