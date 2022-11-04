package io.foundy.hanstargram.view.home.postlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.foundy.hanstargram.base.ViewBindingFragment
import io.foundy.hanstargram.databinding.FragmentPostListBinding
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.setListeners

class PostListFragment : ViewBindingFragment<FragmentPostListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPostListBinding
        get() = FragmentPostListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostAdapter(
            onClickLikeButton = ::onClickLikeButton,
            onClickMoreButton = ::onClickMoreInfoButton
        )
        initRecyclerView(adapter)
    }

    private fun initRecyclerView(adapter: PostAdapter) {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }

            recyclerView.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            recyclerView.layoutManager = LinearLayoutManager(context)

            loadState.setListeners(adapter, swipeRefreshLayout)
        }
    }

    private fun onClickLikeButton(uiState: PostItemUiState) {

    }

    private fun onClickMoreInfoButton(uiState: PostItemUiState) {

    }
}