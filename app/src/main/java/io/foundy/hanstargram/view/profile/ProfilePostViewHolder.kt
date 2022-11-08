package io.foundy.hanstargram.view.profile

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.databinding.ItemProfilePostBinding
import io.foundy.hanstargram.view.home.postlist.PostItemUiState

class ProfilePostViewHolder(
    private val binding: ItemProfilePostBinding,
    private val onClickPost: (PostItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: PostItemUiState) = with(binding) {
        val glide = Glide.with(root)

        glide.load(storageReference.child(uiState.imageUrl))
            .override(200, 200)
            .into(profilePostImage)

        root.setOnClickListener {
            onClickPost(uiState)
        }
    }
}