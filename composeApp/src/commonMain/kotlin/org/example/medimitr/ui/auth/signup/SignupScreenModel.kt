package org.example.medimitr.ui.auth.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.example.medimitr.domain.auth.AuthRepository
import org.example.medimitr.presentation.base.BaseViewModel

// ui/screenmodel/SignupScreenModel.kt
class SignupScreenModel(
    private val authRepository: AuthRepository,
) : BaseViewModel() {
    var name by mutableStateOf("")
    var password by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onSignupClick(onSignUpSuccess: () -> Unit) {
        if (name.isBlank() || password.isBlank() || email.isBlank() || name.isBlank() || address.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }
        isLoading = true
        viewModelScope.launch {
            val result =
                authRepository.signup(
                    name = name,
                    password = password,
                    email = email,
                    phone = phone,
                    address = address,
                )
            isLoading = false
            if (result.isSuccess) {
                onSignUpSuccess()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Signup failed"
            }
        }
    }
}
