package io.foundy.hanstargramwatch.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.R
import io.foundy.hanstargramwatch.databinding.ActivityHomeBinding
import io.foundy.hanstargramwatch.view.common.setListeners
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

        val adapter = PostAdapter(onClickLikeButton = ::onClickLikeButton)
        initRecyclerView(adapter)

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
            // TODO(민성): 팔로우 한 사람이 없는 경우 존재하는 모든 회원 보이기
            setListeners(adapter, binding.swipeToRefresh)
            emptyText.text = getString(R.string.follow_some_people)
            emptyText.textSize = 14.0f
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

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}