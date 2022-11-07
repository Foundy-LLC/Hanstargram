package io.foundy.hanstargramwatch.view.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.foundy.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<WelcomeUiState> =
        MutableStateFlow(WelcomeUiState.None)
    val uiState = _uiState.asStateFlow()

    var name: String = ""

    fun sendInfo() {
        _uiState.update { WelcomeUiState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            val result = UserRepository.saveInitUserInfo(name, null)
            if (result.isSuccess) {
                _uiState.update { WelcomeUiState.SuccessToSave }
            } else {
                _uiState.update { WelcomeUiState.FailedToSave(result.exceptionOrNull()!!) }
            }
        }
    }
}