package io.foundy.hanstargram.view.home.postlist

import androidx.annotation.StringRes
import androidx.paging.PagingData

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