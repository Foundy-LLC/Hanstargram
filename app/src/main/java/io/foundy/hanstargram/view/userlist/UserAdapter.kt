package io.foundy.hanstargram.view.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import io.foundy.hanstargram.databinding.ItemUserBinding

class UserAdapter(
    private val onClickUser: (UserItemUiState) -> Unit
) : PagingDataAdapter<UserItemUiState, UserViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(
            binding,
            onClickUser = onClickUser
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UserItemUiState>() {
            override fun areItemsTheSame(
                oldItem: UserItemUiState,
                newItem: UserItemUiState
            ): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(
                oldItem: UserItemUiState,
                newItem: UserItemUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}