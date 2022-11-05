package io.foundy.hanstargram.view.common

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ContentLoadStateBinding
import java.net.ConnectException

/**
 * [ContentLoadStateBinding]의 프로그래스바, 실패 텍스트, 텅 글자 등을 필요한 순간에 보이게하는 리스너를 설정한다.
 */
fun <PA : PagingDataAdapter<T, VH>, T, VH> ContentLoadStateBinding.setListeners(
    adapter: PA,
    swipeToRefresh: SwipeRefreshLayout,
) {
    swipeToRefresh.setOnRefreshListener { adapter.refresh() }

    this.retryButton.setOnClickListener {
        adapter.retry()
    }

    adapter.addLoadStateListener { loadStates ->
        val refreshLoadState = loadStates.refresh
        val isError = refreshLoadState is LoadState.Error
        val shouldShowEmptyText =
            refreshLoadState is LoadState.NotLoading && adapter.getItemCount() < 1

        emptyText.isVisible = shouldShowEmptyText
        swipeToRefresh.isRefreshing = refreshLoadState is LoadState.Loading
        retryButton.isVisible = isError
        errorMsg.isVisible = isError
        if (refreshLoadState is LoadState.Error) {
            errorMsg.text = when (val exception = refreshLoadState.error) {
                is ConnectException -> root.context.getString(R.string.fail_to_connect)
                else -> exception.message
            }
        }
    }
}
