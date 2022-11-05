package io.foundy.hanstargram.view.home.search

import androidx.paging.PagingData
import io.foundy.hanstargram.repository.model.UserDto

data class SearchUiState(
    val pagingData: PagingData<SearchItemUiState> = PagingData.empty()
)

data class SearchItemUiState(
    val uuid: String,
    val name: String,
    val imageUrl: String?
)

fun UserDto.toSearchItemUiState() = SearchItemUiState(
    uuid = uuid,
    name = name,
    imageUrl = profileImageUrl
)