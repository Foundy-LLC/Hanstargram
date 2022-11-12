package io.foundy.hanstargramwatch.view.explore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.widget.WearableLinearLayoutManager
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.databinding.ActivityExploreBinding
import io.foundy.hanstargramwatch.view.common.PagingLoadStateAdapter
import io.foundy.hanstargramwatch.view.common.setListeners
import io.foundy.hanstargramwatch.view.profile.ProfileActivity
import kotlinx.coroutines.launch

class ExploreActivity : ViewBindingActivity<ActivityExploreBinding>() {

    private val viewModel: ExploreViewModel by viewModels()

    private lateinit var launcher: ActivityResultLauncher<Intent>

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

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setResult(RESULT_OK)
            }
        }

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
        startProfileActivity(uiState.uuid)
    }

    private fun startProfileActivity(userUuid: String) {
        val intent = ProfileActivity.getIntent(this, userUuid)
        launcher.launch(intent)
    }
}