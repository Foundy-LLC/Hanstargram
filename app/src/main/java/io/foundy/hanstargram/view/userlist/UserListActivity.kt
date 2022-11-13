package io.foundy.hanstargram.view.userlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ActivityUserListBinding
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.setListeners
import io.foundy.hanstargram.view.profile.ProfileActivity
import kotlinx.coroutines.launch

class UserListActivity : ViewBindingActivity<ActivityUserListBinding>() {

    private val viewModel: UserListViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityUserListBinding
        get() = ActivityUserListBinding::inflate

    companion object {
        fun getIntent(context: Context, type: UserListPageType, userUuid: String): Intent {
            return Intent(context, UserListActivity::class.java).apply {
                putExtra("type", type)
                putExtra("userUuid", userUuid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userUuid = requireNotNull(intent.getStringExtra("userUuid"))
        val type = intent.getSerializableExtra("type") as UserListPageType
        when (type) {
            UserListPageType.FOLLOWING -> viewModel.bindAsFollowing(userUuid)
            UserListPageType.FOLLOWER -> viewModel.bindAsFollower(userUuid)
        }

        val adapter = UserAdapter(onClickUser = this::onClickUser)
        initRecyclerView(adapter)
        initToolbar(type = type)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    private fun initToolbar(type: UserListPageType) {
        setSupportActionBar(binding.toolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(
                when (type) {
                    UserListPageType.FOLLOWING -> R.string.profile_followee
                    UserListPageType.FOLLOWER -> R.string.profile_follower
                }
            )
        }
    }

    private fun initRecyclerView(adapter: UserAdapter) {
        binding.apply {
            recyclerView.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            recyclerView.layoutManager = LinearLayoutManager(this@UserListActivity)

            loadState.setListeners(adapter, swipeRefreshLayout)
        }
    }

    private fun onClickUser(uiState: UserItemUiState) {
        val intent = ProfileActivity.getIntent(this, userUuid = uiState.uuid)
        startActivity(intent)
    }

    private fun updateUi(uiState: UserListUiState, adapter: UserAdapter) {
        adapter.submitData(lifecycle, uiState.pagingData)
    }
}