package io.foundy.hanstargramwatch.view.home

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.ToggleButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargramwatch.R
import io.foundy.hanstargramwatch.databinding.ItemHomeFooterBinding
import io.foundy.hanstargramwatch.databinding.ItemPostBinding

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onClickUser: (userUuid: String) -> Unit,
    private val onClickLikeButton: (PostModel.ItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: PostModel.ItemUiState) = with(binding) {
        val glide = Glide.with(root)

        glide.load(uiState.writerProfileImageUrl?.let { storageReference.child(it) })
            .fallback(R.drawable.ic_baseline_person_24)
            .into(profileImage)

        userName.text = uiState.writerName

        profileImage.setOnClickListener { onClickUser(uiState.writerUuid) }
        userName.setOnClickListener { onClickUser(uiState.writerUuid) }

        glide.load(storageReference.child(uiState.imageUrl))
            .into(postImage)

        likeToggleButton.isChecked = uiState.meLiked
        likeToggleButton.setOnClickListener {
            val isChecked = (it as ToggleButton).isChecked
            val likeCountText = uiState.likeCount +
                    (if (uiState.meLiked) -1 else 0) +
                    (if (isChecked) 1 else 0)

            likeCount.text = root.context.getString(R.string.like_count, likeCountText)
            onClickLikeButton(uiState)
        }

        likeCount.text = root.context.getString(R.string.like_count, uiState.likeCount)

        @SuppressLint("SetTextI18n")
        val spannable = SpannableString("${uiState.writerName} ${uiState.content}")
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            uiState.writerName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        content.text = spannable
        content.isVisible = uiState.content.isNotEmpty()

        timeAgo.text = uiState.timeAgo
    }
}

class PostFooterViewHolder(
    private val binding: ItemHomeFooterBinding,
    private val onExploreClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.exploreButton.setOnClickListener {
            onExploreClick()
        }
    }
}
