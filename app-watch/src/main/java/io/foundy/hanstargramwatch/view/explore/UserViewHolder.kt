package io.foundy.hanstargramwatch.view.explore

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargramwatch.databinding.ItemUserBinding

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onClickUser: (UserItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: UserItemUiState) = with(binding) {
        val glide = Glide.with(root)

        uiState.profileImageUrl?.let { imageUrl ->
            glide.load(storageReference.child(imageUrl)).into(profileImage)
        }

        name.text = uiState.name

        root.setOnClickListener {
            onClickUser(uiState)
        }
    }
}