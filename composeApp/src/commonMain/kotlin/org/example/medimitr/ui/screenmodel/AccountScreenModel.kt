package org.example.medimitr.ui.screenmodel

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.auth.User
import org.example.medimitr.domain.auth.UserRepository
import org.example.medimitr.presentation.base.BaseScreenModel
import org.example.medimitr.ui.screens.login.AuthCheckScreen

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

class AccountScreenModel(
    private val userRepository: UserRepository,
) : BaseScreenModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserDetails()
    }

    private fun loadUserDetails() {
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        viewModelScope.launch {
            userRepository
                .getCurrentUser()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "Failed to load user data: ${e.message}",
                        )
                    }
                }.collect { result ->
                    if (result.isSuccess) {
                        val user = result.getOrThrow()
                        _uiState.update { it.copy(isLoading = false, user = user) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = "Failed to load user data: ${result.exceptionOrNull()?.message}",
                            )
                        }
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
            val resultFlow =
                when (field) {
                    EditingField.EMAIL -> userRepository.updateEmail(newValue)
                    EditingField.ADDRESS -> userRepository.updateAddress(newValue)
                    EditingField.PHONE -> userRepository.updatePhone(newValue)
                    EditingField.NONE -> return@launch // Should not happen
                }

            resultFlow
                .catch { e -> handleUpdateResult(Result.failure(e)) }
                .collect { result -> handleUpdateResult(result) }
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
            userRepository
                .changePassword(oldPass, newPass)
                .catch { e -> handleUpdateResult(Result.failure(e), passwordChange = true) }
                .collect { result -> handleUpdateResult(result, passwordChange = true) }
        }
    }

    fun logout(navigator: Navigator) {
        _uiState.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            userRepository.logout()
            navigator.replaceAll(AuthCheckScreen()) // Assuming LoginScreen is the screen to navigate to after logout
            // Logout state / navigation will likely be handled by observing
            // the overall auth state at a higher level in the app,
            // but we set the flag here.
            // Resetting flag after short delay or handled by external observer
            // kotlinx.coroutines.delay(1000)
            // _uiState.update { it.copy(isLoggingOut = false) }
        }
    }

    private fun handleUpdateResult(
        result: Result<Unit>,
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
