package io.foundy.hanstargram.view.home.postlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingFragment
import io.foundy.hanstargram.databinding.FragmentPostListBinding
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.setListeners
import kotlinx.coroutines.launch

class PostListFragment : ViewBindingFragment<FragmentPostListBinding>() {

    private val viewModel: PostListViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPostListBinding
        get() = FragmentPostListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostAdapter(
            onClickLikeButton = ::onClickLikeButton,
            onClickMoreButton = ::onClickMoreInfoButton
        )
        initRecyclerView(adapter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    private fun initRecyclerView(adapter: PostAdapter) {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }

            recyclerView.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            recyclerView.layoutManager = LinearLayoutManager(context)

            loadState.setListeners(adapter, swipeRefreshLayout)
            loadState.emptyText.text = getString(R.string.follow_some_people)
            loadState.emptyText.textSize = 20.0f
        }
    }

    private fun updateUi(uiState: PostListUiState, adapter: PostAdapter) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uiState.pagingData)
    }

    private fun onClickLikeButton(uiState: PostItemUiState) {
        viewModel.toggleLike(postUuid = uiState.uuid)
    }

    private fun onClickMoreInfoButton(uiState: PostItemUiState) {
        // TODO: 구현
    }
}