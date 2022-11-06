package io.foundy.hanstargram.view.profile

import io.foundy.hanstargram.repository.model.PostDto

data class ProfileUiState(
    val profileInfo: ProfileInfoUiState = ProfileInfoUiState(),
    val profilePost: MutableList<PostDto> = mutableListOf(),
    val message : String = "message"
)

data class ProfileInfoUiState(
    val name: String = "",
    val introduce: String = "",
    val profileImg: String = "",
    val post: Long = 0,
    val follower: Long = 0,
    val followee: Long = 0,
    val isMine: Boolean = true
)