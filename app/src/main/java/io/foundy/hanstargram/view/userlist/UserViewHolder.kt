package io.foundy.hanstargram.view.userlist

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ItemUserBinding

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onClickUser: (UserItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: UserItemUiState) = with(binding) {
        val glide = com.bumptech.glide.Glide.with(root)

        glide.load(uiState.profileImageUrl?.let { storageReference.child(it) })
            .fallback(R.drawable.ic_baseline_person_24)
            .into(profileImage)

        name.text = uiState.name

        root.setOnClickListener {
            onClickUser(uiState)
        }
    }
}