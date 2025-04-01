package org.example.medimitr.ui.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.example.medimitr.domain.auth.AuthRepository
import org.example.medimitr.presentation.base.BaseScreenModel

// ui/screenmodel/LoginScreenModel.kt
class LoginScreenModel(
    private val authRepository: AuthRepository,
) : BaseScreenModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onLoginClick() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }
        isLoading = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            isLoading = false
            if (result.isSuccess) {
                // Automatically navigate to the main screen after successful login
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }
}
