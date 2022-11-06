package io.foundy.hanstargram.view.profile

import io.foundy.data.model.PostDto
import io.foundy.data.model.UserDto

data class ProfileUiState(
    val userInfo: UserDto = UserDto("", "", "", "", "0c8b55d6ef764c46bd0615661f9bf2bf.png"),
    val userIntroduce : String = "",
    val postList : MutableList<PostDto> = mutableListOf(),
    val post : Int = 0,
    val follower : Int = 0,
    val followee: Int = 0,
    val state : Int = 0,
    val userMessage : Int? = null,
)