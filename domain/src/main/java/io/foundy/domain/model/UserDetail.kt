package io.foundy.domain.model

data class UserDetail(
    val uuid: String,
    val name: String,
    val introduce: String,
    val email: String?,
    val profileImageUrl: String?,
    val postCount: Long,
    val followersCount: Long,
    val followingCount: Long,
    val isCurrentUserFollowing: Boolean,
    val isMe: Boolean
) : java.io.Serializable
