package io.foundy.hanstargram.repository.model

import java.util.*

data class PostDto(
    val uuid: String,
    val writerUuid: String,
    val content: String,
    val dateTime: Date,
    val imageUrl: String
)
