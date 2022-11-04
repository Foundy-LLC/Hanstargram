package io.foundy.hanstargram.view.home.postlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.ItemPostBinding

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onClickLikeButton: (PostItemUiState) -> Unit,
    private val onClickMoreButton: (PostItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(uiState: PostItemUiState) = with(binding) {
        val glide = Glide.with(root)

        glide.load(uiState.writerProfileImageUrl)
            .into(profileImage)

        userName.text = uiState.writerName

        moreInfoButton.setOnClickListener {
            onClickMoreButton(uiState)
        }

        glide.load(uiState.imageUrl)
            .into(postImage)

        content.text = uiState.content

        // TODO(민성): 좋아요 누른 경우 빨갛게 채운 모양 보이기
        likeButton.setOnClickListener {
            onClickLikeButton(uiState)
        }

        likeCount.text = root.context.getString(R.string.like_count, uiState.likeCount)
    }
}
