package io.foundy.hanstargram.repository.model

data class UserDto(
    val uuid: String,
    val name: String,
    val email: String,
    val password: String,
    val profileImageUrl: String
)
