package io.foundy.hanstargramwatch.view.explore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.widget.WearableLinearLayoutManager
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.databinding.ActivityExploreBinding
import io.foundy.hanstargramwatch.view.common.PagingLoadStateAdapter
import io.foundy.hanstargramwatch.view.common.setListeners
import kotlinx.coroutines.launch

class ExploreActivity : ViewBindingActivity<ActivityExploreBinding>() {

    private val viewModel: ExploreViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityExploreBinding
        get() = ActivityExploreBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ExploreActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = UserAdapter(onClickUser = ::onClickUser)
        initRecyclerView(adapter)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    private fun initRecyclerView(adapter: UserAdapter) {
        binding.recyclerView.apply {
            this.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            this.layoutManager = WearableLinearLayoutManager(this@ExploreActivity)
        }
        binding.loadState.setListeners(adapter, binding.swipeToRefresh)
    }

    private fun updateUi(uiState: ExploreUiState, adapter: UserAdapter) {
        adapter.submitData(lifecycle, uiState.pagingData)
    }

    private fun onClickUser(uiState: UserItemUiState) {
        // TODO(민성): show user profile
    }
}