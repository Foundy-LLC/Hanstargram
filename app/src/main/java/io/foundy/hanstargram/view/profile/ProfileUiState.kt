package io.foundy.hanstargram.view.profile

import io.foundy.data.model.PostDto
import io.foundy.data.model.UserDto

data class ProfileUiState(
    val userInfo: UserDto = UserDto("", "", "", "", ""),
    val userIntroduce : String = "",
    val postList : MutableList<PostDto> = mutableListOf(),
    val post : Int = 0,
    val follower : Int = 0,
    val followee: Int = 0,
    val message : String = "몰라",
    val isMine : Boolean = false
)