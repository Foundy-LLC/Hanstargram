package io.foundy.hanstargram.view.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ActivityProfileBinding
import io.foundy.hanstargram.view.home.postlist.PostListFragment

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

    private fun getUserUuid(): String {
        return intent.getStringExtra("userUuid")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.bindProfile(getUserUuid())

        changeToProfileFragment()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
        return super.onSupportNavigateUp()
    }

    private fun changeToProfileFragment() {
        val fragmentManager = supportFragmentManager
        val profileFragment = ProfileFragment(
            onClickPost = {
                replaceToUserPostListFragment()
            }
        )
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view, profileFragment)
            addToBackStack(null)
        }.commit()
    }

    private fun replaceToUserPostListFragment() {
        val postPagingData = viewModel.profilePostUiState.value.pagingData
        val fragmentManager = supportFragmentManager
        // TODO: 클릭한 게시글의 인덱스로 스크롤 포지션 변경하기
        val postListFragment = PostListFragment(
            toolbarTitle = "",
            postPagingData = postPagingData
        )
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view, postListFragment)
            addToBackStack(null)
        }.commit()
    }
}