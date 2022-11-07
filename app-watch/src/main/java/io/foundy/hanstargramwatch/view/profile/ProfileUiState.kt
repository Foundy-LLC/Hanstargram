package io.foundy.hanstargramwatch.view.profile

import io.foundy.domain.model.UserDetail

data class ProfileUiState(
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = true,
    val userMessage: String? = null
)
