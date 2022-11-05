package io.foundy.hanstargram.repository.model

data class LikeDto(
    val uuid: String = "",
    val likerUuid: String = "",
    val postUuid: String = ""
)