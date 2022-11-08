package io.foundy.hanstargram.view.profile

import androidx.paging.PagingData
import io.foundy.domain.model.Post
import io.foundy.domain.model.UserDetail
import io.foundy.hanstargram.view.home.postlist.PostItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ProfilePostUiState(
    val pagingData: PagingData<PostItemUiState> = PagingData.empty()
)

data class ProfileDetailUiState(
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = true,
    val userMessage: String? = null
)