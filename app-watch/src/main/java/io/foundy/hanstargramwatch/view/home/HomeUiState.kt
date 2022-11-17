package io.foundy.hanstargramwatch.view.home

import androidx.annotation.StringRes
import androidx.paging.PagingData
import io.foundy.domain.model.Post

data class HomeUiState(
    val pagingData: PagingData<PostModel> = PagingData.empty(),
    @StringRes
    val userMessage: Int? = null
)

sealed class PostModel {

    data class ItemUiState(
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
    ) : PostModel()

    object Footer : PostModel()
}

fun Post.toUiState() = PostModel.ItemUiState(
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
