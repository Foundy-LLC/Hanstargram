package io.foundy.hanstargram.view.home.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ItemSearchBinding

class SearchViewHolder(
    private val binding: ItemSearchBinding,
    private val onClickUser: (SearchItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: SearchItemUiState) = with(binding) {
        val glide = Glide.with(root)

        glide
            .load(uiState.imageUrl?.let { storageReference.child(it) })
            .fallback(R.drawable.ic_baseline_person_24)
            .into(profileImage)

        name.text = uiState.name

        root.setOnClickListener {
            onClickUser(uiState)
        }
    }
}