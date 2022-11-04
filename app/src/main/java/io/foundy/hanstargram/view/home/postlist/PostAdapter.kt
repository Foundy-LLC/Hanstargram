package io.foundy.hanstargram.view.home.postlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.foundy.hanstargram.databinding.ItemPostBinding

class PostAdapter(
    private val onClickLikeButton: (PostItemUiState) -> Unit,
    private val onClickMoreButton: (PostItemUiState) -> Unit
) : PagingDataAdapter<PostItemUiState, PostViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(
            binding,
            onClickLikeButton = onClickLikeButton,
            onClickMoreButton = onClickMoreButton
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PostItemUiState>() {
            override fun areItemsTheSame(
                oldItem: PostItemUiState,
                newItem: PostItemUiState
            ): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(
                oldItem: PostItemUiState,
                newItem: PostItemUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
