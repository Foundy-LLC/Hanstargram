package io.foundy.hanstargram.repository.model

data class FollowDto(
    val uuid: String,
    val followerUuid: String,
    val followeeUuid: String
)
