package io.foundy.hanstargram.view.userlist

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.databinding.ItemUserBinding

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onClickUser: (UserItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: UserItemUiState) = with(binding) {
        val glide = com.bumptech.glide.Glide.with(root)

        uiState.profileImageUrl?.let { imageUrl ->
            glide.load(storageReference.child(imageUrl)).into(profileImage)
        }

        name.text = uiState.name

        root.setOnClickListener {
            onClickUser(uiState)
        }
    }
}