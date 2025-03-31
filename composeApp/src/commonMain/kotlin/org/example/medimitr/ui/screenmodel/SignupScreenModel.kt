package org.example.medimitr.ui.screenmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.example.medimitr.domain.auth.AuthRepository
import org.example.medimitr.presentation.base.BaseScreenModel
import org.example.medimitr.ui.screens.MainScreen

// ui/screenmodel/SignupScreenModel.kt
class SignupScreenModel(
    private val authRepository: AuthRepository,
) : BaseScreenModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var email by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onSignupClick(navigator: Navigator) {
        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }
        isLoading = true
        viewModelScope.launch {
            val result = authRepository.signup(username, password, email)
            isLoading = false
            if (result.isSuccess) {
                navigator.replaceAll(MainScreen())
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Signup failed"
            }
        }
    }
}
