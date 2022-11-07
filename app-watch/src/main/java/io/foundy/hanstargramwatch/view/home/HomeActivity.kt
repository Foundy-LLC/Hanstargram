package io.foundy.hanstargramwatch.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.R
import io.foundy.hanstargramwatch.databinding.ActivityHomeBinding
import io.foundy.hanstargramwatch.view.common.setListeners
import io.foundy.hanstargramwatch.view.explore.ExploreActivity
import io.foundy.hanstargramwatch.view.profile.ProfileActivity
import kotlinx.coroutines.launch

class HomeActivity : ViewBindingActivity<ActivityHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = PostAdapter(
            onClickLikeButton = ::onClickLikeButton,
            onClickUser = ::onClickUser
        )
        initRecyclerView(adapter)

        binding.exploreButton.setOnClickListener {
            startExploreActivity()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    private fun initRecyclerView(adapter: PostAdapter) {
        binding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(this@HomeActivity)
        }
        binding.loadState.apply {
            setListeners(adapter, binding.swipeToRefresh)
            emptyText.text = getString(R.string.follow_some_people)
            emptyText.textSize = 14.0f

            adapter.addLoadStateListener { loadStates ->
                val isEmpty =
                    loadStates.refresh is LoadState.NotLoading && adapter.itemCount < 1
                binding.exploreButton.isVisible = isEmpty
            }
        }
    }

    private fun updateUi(uiState: HomeUiState, adapter: PostAdapter) {
        adapter.submitData(lifecycle, uiState.pagingData)
        if (uiState.userMessage != null) {
            showSnackBar(getString(uiState.userMessage))
            viewModel.userMessageShown()
        }
    }

    private fun onClickLikeButton(uiState: PostItemUiState) {
        viewModel.toggleLike(postUuid = uiState.uuid)
    }

    private fun onClickUser(userUuid: String) {
        startProfileActivity(userUuid)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun startProfileActivity(userUuid: String) {
        val intent = ProfileActivity.getIntent(this, userUuid)
        startActivity(intent)
    }

    private fun startExploreActivity() {
        val intent = ExploreActivity.getIntent(this)
        startActivity(intent)
    }
}