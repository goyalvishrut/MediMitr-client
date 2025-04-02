package org.example.medimitr.ui.account.screenmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.auth.User
import org.example.medimitr.domain.auth.UserRepository
import org.example.medimitr.presentation.base.BaseViewModel

// Enum to track which field is being edited inline
enum class EditingField { NONE, EMAIL, ADDRESS, PHONE }

data class AccountUiState(
    // Data loading
    val isLoading: Boolean = true,
    val user: User? = null,
    val loadError: String? = null,
    // Editing state
    val currentlyEditing: EditingField = EditingField.NONE,
    val showPasswordChangeDialog: Boolean = false,
    // Update operation state
    val isUpdating: Boolean = false,
    val updateError: String? = null, // Error during save/password change
    val updateSuccessMessage: String? = null,
    // Logout state
    val isLoggingOut: Boolean = false,
)

class AccountSettingViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserDetails()
    }

    private fun loadUserDetails() {
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "User not found.",
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            loadError = null,
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = "Failed to load user details.",
                    )
                }
            }
        }
    }

    fun startEditing(field: EditingField) {
        _uiState.update {
            it.copy(
                currentlyEditing = field,
                updateError = null,
                updateSuccessMessage = null,
            )
        }
    }

    fun showPasswordDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showPasswordChangeDialog = show,
                updateError = null,
                updateSuccessMessage = null,
            )
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                currentlyEditing = EditingField.NONE,
                updateError = null,
                updateSuccessMessage = null,
            )
        }
    }

    fun saveField(
        field: EditingField,
        newValue: String,
    ) {
        _uiState.update {
            it.copy(
                isUpdating = true,
                updateError = null,
                updateSuccessMessage = null,
            )
        }
        viewModelScope.launch {
            val result =
                when (field) {
                    EditingField.EMAIL -> userRepository.updateEmail(newValue)
                    EditingField.ADDRESS -> userRepository.updateAddress(newValue)
                    EditingField.PHONE -> userRepository.updatePhone(newValue)
                    EditingField.NONE -> return@launch // Should not happen
                }

            handleUpdateResult(result)
        }
    }

    fun changePassword(
        oldPass: String,
        newPass: String,
    ) {
        _uiState.update {
            it.copy(
                isUpdating = true,
                updateError = null,
                updateSuccessMessage = null,
            )
        }
        viewModelScope.launch {
            handleUpdateResult(userRepository.changePassword(oldPass, newPass), true)
        }
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            try {
                userRepository.logout()
                // **DO NOT NAVIGATE HERE**
                // The change in auth status observed at the root will handle navigation.
                // Optionally reset the loading state if needed, although the screen
                // will likely disappear due to root recomposition.
                // _uiState.update { it.copy(isLoggingOut = false) }
            } catch (e: Exception) {
                // Handle logout error (e.g., show message)
                _uiState.update {
                    it.copy(
                        isLoggingOut = false,
                        updateError = "Logout failed: ${e.message}",
                    )
                }
            }
            // Assuming LoginScreen is the screen to navigate to after logout
            // Logout state / navigation will likely be handled by observing
            // the overall auth state at a higher level in the app,
            // but we set the flag here.
            // Resetting flag after short delay or handled by external observer
            // kotlinx.coroutines.delay(1000)
            // _uiState.update { it.copy(isLoggingOut = false) }
        }
    }

    private fun handleUpdateResult(
        result: Result<Boolean>,
        passwordChange: Boolean = false,
    ) {
        result
            .onSuccess {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        currentlyEditing = EditingField.NONE, // Exit edit mode on success
                        updateError = null,
                        showPasswordChangeDialog = if (passwordChange) false else it.showPasswordChangeDialog,
                        updateSuccessMessage = if (passwordChange) "Password changed successfully" else "Field updated successfully",
                    )
                }
                loadUserDetails() // Refresh user details after successful update
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        // Keep editing mode open on error? Optional.
                        // currentlyEditing = EditingField.NONE,
                        updateError = "Update failed: ${e.message}",
                    )
                }
            }
    }

    // Clear success/error messages after a delay or on next action
    fun clearMessages() {
        _uiState.update { it.copy(updateError = null, updateSuccessMessage = null) }
    }
}
