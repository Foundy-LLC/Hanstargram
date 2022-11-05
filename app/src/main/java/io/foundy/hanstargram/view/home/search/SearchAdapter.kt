package io.foundy.hanstargram.view.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.foundy.hanstargram.databinding.ItemSearchBinding

class SearchAdapter(
    private val onClickUser: (SearchItemUiState) -> Unit
) : PagingDataAdapter<SearchItemUiState, SearchViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
        return SearchViewHolder(
            binding,
            onClickUser = onClickUser
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SearchItemUiState>() {
            override fun areItemsTheSame(
                oldItem: SearchItemUiState,
                newItem: SearchItemUiState
            ): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(
                oldItem: SearchItemUiState,
                newItem: SearchItemUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}