package io.foundy.hanstargram.view.home.postlist

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
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ItemPostBinding

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onClickLikeButton: (PostItemUiState) -> Unit,
    private val onClickMoreButton: (PostItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: PostItemUiState) = with(binding) {
        val glide = Glide.with(root)

        glide.load(uiState.writerProfileImageUrl?.let { storageReference.child(it) })
            .into(profileImage)

        userName.text = uiState.writerName

        moreInfoButton.isVisible = uiState.isMine
        moreInfoButton.setOnClickListener {
            onClickMoreButton(uiState)
        }

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

        timeAgo.text = uiState.timeAgo
    }
}
