package io.foundy.hanstargramwatch.view.home

import androidx.annotation.StringRes
import androidx.paging.PagingData
import io.foundy.domain.model.Post

data class HomeUiState(
    val pagingData: PagingData<PostItemUiState> = PagingData.empty(),
    @StringRes
    val userMessage: Int? = null
)

data class PostItemUiState(
    val uuid: String,
    val writerUuid: String,
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
    writerUuid = writerUuid,
    writerName = writerName,
    writerProfileImageUrl = writerProfileImageUrl,
    content = content,
    imageUrl = imageUrl,
    likeCount = likeCount,
    meLiked = meLiked,
    isMine = isMine,
    timeAgo = timeAgo
)
