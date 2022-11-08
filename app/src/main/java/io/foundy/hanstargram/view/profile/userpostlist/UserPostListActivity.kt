package io.foundy.hanstargram.view.profile.userpostlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import io.foundy.common.base.ViewBindingActivity
import io.foundy.domain.model.UserDetail
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ActivityUserPostListBinding
import io.foundy.hanstargram.view.home.postlist.PostListFragment

class UserPostListActivity : ViewBindingActivity<ActivityUserPostListBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityUserPostListBinding
        get() = ActivityUserPostListBinding::inflate

    companion object {
        fun getIntent(context: Context, userDetail: UserDetail, postUuid: String): Intent {
            return Intent(context, UserPostListActivity::class.java).apply {
                putExtra("userDetail", userDetail)
                putExtra("postUuid", postUuid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDetail = intent.getSerializableExtra("userDetail") as UserDetail
        val fragmentManager = supportFragmentManager
        val postListFragment = PostListFragment(
            toolbarTitle = userDetail.name,
            userUuid = userDetail.uuid
        )
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container_view, postListFragment)
        fragmentTransaction.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}