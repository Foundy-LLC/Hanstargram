package io.foundy.hanstargram.view.home.postlist

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.foundy.common.base.ViewBindingFragment
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.FragmentPostListBinding
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.registerObserverForScrollToTop
import io.foundy.hanstargram.view.common.setListeners
import io.foundy.hanstargram.view.posting.PostingActivity
import io.foundy.hanstargram.view.profile.ProfileActivity
import kotlinx.coroutines.launch

class PostListFragment(
    private val toolbarTitle: String? = null,
    /**
     * 이 값이 전달되는 경우 [userUuid] 회원의 게시물만 보인다.
     */
    private val userUuid: String? = null,
) : ViewBindingFragment<FragmentPostListBinding>() {

    private val viewModel: PostListViewModel by viewModels()

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPostListBinding
        get() = FragmentPostListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.bind(userUuid)

        val adapter = PostAdapter(
            onClickLikeButton = ::onClickLikeButton,
            onClickMoreButton = ::onClickMoreInfoButton,
            onClickUser = ::onClickUser
        )
        initToolbar()
        initRecyclerView(adapter)
        initBottomSheetDialog(adapter)

        binding.toolbarPostButton.setOnClickListener {
            startPostingActivity()
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                adapter.refresh()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    private fun initToolbar() {
        if (toolbarTitle != null) {
            val activity = requireActivity() as AppCompatActivity
            activity.setSupportActionBar(binding.toolBar)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                title = this@PostListFragment.toolbarTitle
            }
            binding.toolbarTitle.isVisible = false
        }
    }

    private fun initBottomSheetDialog(adapter: PostAdapter) {
        bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.bottom_sheet_post_item)
        }
        bottomSheetDialog.behavior.isDraggable = false

        val editButton = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.editBar)
        editButton?.setOnClickListener {
            bottomSheetDialog.hide()
            // TODO: 게시글 수정하는 화면으로 전환하기
        }

        val removeButton = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.removeBar)
        removeButton?.setOnClickListener {
            bottomSheetDialog.hide()
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(getString(R.string.delete_post))
                setMessage(R.string.are_you_sure_you_want_to_delete)
                setNegativeButton(R.string.cancel) { _, _ -> }
                setPositiveButton(R.string.delete) { _, _ ->
                    viewModel.deleteSelectedPost(onDeleted = { adapter.refresh() })
                }
            }.show()
        }
    }

    private fun initRecyclerView(adapter: PostAdapter) {
        binding.apply {
            recyclerView.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            recyclerView.layoutManager = LinearLayoutManager(context)

            loadState.setListeners(adapter, swipeRefreshLayout)
            loadState.emptyText.text = getString(R.string.follow_some_people)
            loadState.emptyText.textSize = 20.0f

            adapter.registerObserverForScrollToTop(recyclerView)
        }
    }

    private fun updateUi(uiState: PostListUiState, adapter: PostAdapter) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uiState.pagingData)
        if (uiState.userMessage != null) {
            showSnackBar(getString(uiState.userMessage))
            viewModel.userMessageShown()
        }
    }

    private fun onClickLikeButton(uiState: PostItemUiState) {
        viewModel.toggleLike(postUuid = uiState.uuid)
    }

    private fun onClickMoreInfoButton(uiState: PostItemUiState) {
        viewModel.showPostOptionBottomSheet(uiState)
        bottomSheetDialog.show()
    }

    private fun onClickUser(uiState: PostItemUiState) {
        startProfileActivity(uiState.writerUuid)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun startProfileActivity(userUuid: String) {
        val intent = ProfileActivity.getIntent(requireContext(), userUuid)
        startActivity(intent)
    }

    private fun startPostingActivity() {
        val intent = PostingActivity.getIntent(requireContext())
        launcher.launch(intent)
    }
}