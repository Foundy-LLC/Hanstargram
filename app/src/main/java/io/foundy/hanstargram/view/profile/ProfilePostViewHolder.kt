package io.foundy.hanstargram.view.profile

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.databinding.ItemSearchBinding

class ProfilePostViewHolder(
    private val binding: ItemSearchBinding,// Todo: 이거 뭔지 확인
    private val onClickUser: (ProfilePostItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: ProfilePostItemUiState) = with(binding) {
        val glide = Glide.with(root)

        uiState.imageUrl?.let { imageUrl ->
            glide.load(storageReference.child(imageUrl)).into(profileImage)
        }

        root.setOnClickListener {
            onClickUser(uiState)
        }
    }
}