package com.example.cafex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafex.model.User
import com.example.cafex.repository.AuthRepository
import com.example.cafex.utils.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val user: User? = null,
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val isFirebaseEnabled: Boolean = false,
    val isDatabaseEnabled: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
)

class AuthViewModel(
    private val repository: AuthRepository,
    isFirebaseEnabled: Boolean,
    isDatabaseEnabled: Boolean,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AuthUiState(
            isFirebaseEnabled = isFirebaseEnabled,
            isDatabaseEnabled = isDatabaseEnabled,
        ),
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        isCheckingSession = false,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun login(email: String, password: String) {
        val validationError = Validation.emailError(email) ?: Validation.passwordError(password)
        if (validationError != null) {
            showError(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            repository.login(email, password).fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                },
                onFailure = { error ->
                    showError(error.readableMessage("Unable to sign in."))
                },
            )
        }
    }

    fun register(fullName: String, email: String, password: String, confirmPassword: String) {
        val validationError = Validation.registrationError(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
        )
        if (validationError != null) {
            showError(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            repository.register(fullName, email, password).fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                },
                onFailure = { error ->
                    showError(error.readableMessage("Unable to create your account."))
                },
            )
        }
    }

    fun sendPasswordReset(email: String) {
        val validationError = Validation.emailError(email)
        if (validationError != null) {
            showError(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            repository.sendPasswordReset(email).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            infoMessage = if (it.isFirebaseEnabled) {
                                "Password reset email sent."
                            } else {
                                "Demo mode: password reset simulated."
                            },
                        )
                    }
                },
                onFailure = { error ->
                    showError(error.readableMessage("Unable to send a reset email."))
                },
            )
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update { it.copy(user = null, infoMessage = null, errorMessage = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message, infoMessage = null) }
    }
}

private fun Throwable.readableMessage(fallback: String): String =
    localizedMessage?.takeIf { it.isNotBlank() } ?: fallback
