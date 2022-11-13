package io.foundy.hanstargram.view.userlist

import androidx.paging.PagingData
import io.foundy.data.model.UserDto

enum class UserListPageType : java.io.Serializable {
    FOLLOWING,
    FOLLOWER
}

data class UserListUiState(
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