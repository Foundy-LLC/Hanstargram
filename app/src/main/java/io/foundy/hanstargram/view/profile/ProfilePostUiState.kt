package io.foundy.hanstargram.view.profile

import androidx.paging.PagingData
import io.foundy.data.model.PostDto
import io.foundy.domain.model.UserDetail

data class ProfilePostUiState(
    val pagingData: PagingData<ProfilePostItemUiState> = PagingData.empty()
)

data class ProfilePostItemUiState(
    val uuid: String,
    val imageUrl: String?
)

fun PostDto.toProfilePostItemUiState() = ProfilePostItemUiState(
    uuid = uuid,
    imageUrl = imageUrl
)

data class ProfileDetailUiState(
    val userDetails: UserDetail? = null,
    val isLoading: Boolean = true,
    val isFollowing: Boolean? = null,
    val userMessage: Int? = null
)