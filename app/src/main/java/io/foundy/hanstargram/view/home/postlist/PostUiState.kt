package io.foundy.hanstargram.view.home.postlist

import androidx.annotation.StringRes
import androidx.paging.PagingData
import io.foundy.domain.model.Post

data class PostListUiState(
    val pagingData: PagingData<PostItemUiState> = PagingData.empty(),
    val selectedPostItem: PostItemUiState? = null,
    @StringRes
    val userMessage: Int? = null
)

data class PostItemUiState(
    val uuid: String,
    val writerName: String,
    val writerProfileImageUrl: String?,
    val content: String,
    val imageUrl: String,
    val likeCount: Int,
    val meLiked: Boolean,
    val isMine: Boolean,
    val timeAgo: String
)

fun Post.toUiState() = PostItemUiState(
    uuid = uuid,
    writerName = writerName,
    writerProfileImageUrl = writerProfileImageUrl,
    content = content,
    imageUrl = imageUrl,
    likeCount = likeCount,
    meLiked = meLiked,
    isMine = isMine,
    timeAgo = timeAgo
)
