package io.foundy.hanstargramwatch.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.foundy.hanstargramwatch.databinding.ItemHomeFooterBinding
import io.foundy.hanstargramwatch.databinding.ItemPostBinding

class PostAdapter(
    private val onClickUser: (userUuid: String) -> Unit,
    private val onClickLikeButton: (PostModel.ItemUiState) -> Unit,
    private val onExploreClick: () -> Unit
) : PagingDataAdapter<PostModel, RecyclerView.ViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PostModel.ItemUiState -> {
            LIST_TYPE
        }
        PostModel.Footer -> {
            FOOTER_TYPE
        }
        else -> {
            throw IllegalArgumentException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LIST_TYPE -> {
                val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
                PostViewHolder(
                    binding,
                    onClickUser = onClickUser,
                    onClickLikeButton = onClickLikeButton
                )
            }
            FOOTER_TYPE -> {
                val binding = ItemHomeFooterBinding.inflate(layoutInflater, parent, false)
                PostFooterViewHolder(
                    binding,
                    onExploreClick = onExploreClick
                )
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is PostModel.ItemUiState -> {
                    (holder as PostViewHolder).bind(it)
                }
                PostModel.Footer -> {
                    (holder as PostFooterViewHolder).bind()
                }
            }
        }
    }

    companion object {

        private const val LIST_TYPE = 0
        private const val FOOTER_TYPE = 1

        private val diffCallback = object : DiffUtil.ItemCallback<PostModel>() {
            override fun areItemsTheSame(
                oldItem: PostModel,
                newItem: PostModel
            ): Boolean {
                if (oldItem is PostModel.ItemUiState && newItem is PostModel.ItemUiState) {
                    return oldItem.uuid == newItem.uuid
                }
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostModel,
                newItem: PostModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}