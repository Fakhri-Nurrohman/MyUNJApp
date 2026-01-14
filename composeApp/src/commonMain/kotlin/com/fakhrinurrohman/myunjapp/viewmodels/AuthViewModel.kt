package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val nim: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isSyncing = repository.isSyncing
    val isLoggedIn = repository.isLoggedIn

    fun onNimChange(nim: String) {
        _uiState.value = _uiState.value.copy(nim = nim, error = null)
    }

    fun handleSyncAction(onLoginRequired: () -> Unit) {
        if (repository.isLoggedIn) {
            viewModelScope.launch {
                repository.performSync()
            }
        } else {
            onLoginRequired()
        }
    }

    fun login(password: String) {
        val nim = _uiState.value.nim
        if (nim.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "NIM and Password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.loginAndSync(nim, password)
                if (response.success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message ?: "Login failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Connection error: ${e.message}"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
