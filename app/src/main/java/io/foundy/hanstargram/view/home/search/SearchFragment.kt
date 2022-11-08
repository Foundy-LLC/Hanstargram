package io.foundy.hanstargram.view.home.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import io.foundy.common.base.ViewBindingFragment
import io.foundy.hanstargram.databinding.FragmentSearchBinding
import io.foundy.hanstargram.util.debounce
import io.foundy.hanstargram.view.common.PagingLoadStateAdapter
import io.foundy.hanstargram.view.common.setListeners
import io.foundy.hanstargram.view.profile.ProfileActivity
import kotlinx.coroutines.launch

class SearchFragment : ViewBindingFragment<FragmentSearchBinding>() {
    private val viewModel: SearchViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SearchAdapter(onClickUser = ::onClickUser)
        initRecyclerView(adapter)

        if (binding.queryInput.requestFocus()) {
            showSoftKeyboard()
        }

        val debounceTextChange = debounce(300L, viewModel.viewModelScope, viewModel::searchUser)
        binding.queryInput.addTextChangedListener {
            if (it != null) {
                binding.clearButton.isVisible = it.isNotEmpty()
                debounceTextChange(it.toString())
            }
        }
        binding.queryInput.setOnEditorActionListener { textView, actionId, _ ->
            val query = textView.text
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    if (query != null) {
                        viewModel.searchUser(query.toString())
                    }
                }
                else -> {
                    return@setOnEditorActionListener false
                }
            }
            true
        }

        binding.clearButton.setOnClickListener {
            binding.queryInput.setText("")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    private fun initRecyclerView(adapter: SearchAdapter) {
        binding.apply {
            recyclerView.adapter = adapter.withLoadStateFooter(
                PagingLoadStateAdapter { adapter.retry() }
            )
            recyclerView.layoutManager =
                GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

            loadState.setListeners(adapter, swipeRefreshLayout)
        }
    }

    private fun showSoftKeyboard() {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(binding.queryInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun updateUi(uiState: SearchUiState, adapter: SearchAdapter) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uiState.pagingData)
    }

    private fun onClickUser(uiState: SearchItemUiState) {
        val intent = ProfileActivity.getIntent(requireContext(), uiState.uuid)
        startActivity(intent)
    }
}