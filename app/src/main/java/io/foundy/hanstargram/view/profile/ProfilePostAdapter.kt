package io.foundy.hanstargram.view.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.foundy.hanstargram.databinding.ItemProfilePostBinding

class ProfilePostAdapter(
    private val onClickPost: (ProfilePostItemUiState) -> Unit
) : PagingDataAdapter<ProfilePostItemUiState, ProfilePostViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProfilePostBinding.inflate(layoutInflater, parent, false)
        return ProfilePostViewHolder(
            binding,
            onClickPost = onClickPost
        )
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ProfilePostItemUiState>() {
            override fun areItemsTheSame(
                oldItem: ProfilePostItemUiState,
                newItem: ProfilePostItemUiState
            ): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(
                oldItem: ProfilePostItemUiState,
                newItem: ProfilePostItemUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}