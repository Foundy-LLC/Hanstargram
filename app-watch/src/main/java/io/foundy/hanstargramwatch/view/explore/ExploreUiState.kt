package io.foundy.hanstargramwatch.view.explore

import androidx.paging.PagingData
import io.foundy.data.model.UserDto

data class ExploreUiState(
    val pagingData: PagingData<UserItemUiState> = PagingData.empty()
)

data class UserItemUiState(
    val uuid: String,
    val name: String,
    val profileImageUrl: String?
)

fun UserDto.toUiState() = UserItemUiState(
    uuid = uuid,
    name = name,
    profileImageUrl = profileImageUrl
)
