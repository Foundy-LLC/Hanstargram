package io.foundy.data.model

data class UserDto(
    val uuid: String = "",
    val name: String = "",
    val email: String? = null,
    val password: String? = null,
    val profileImageUrl: String? = null
)
