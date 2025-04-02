package org.example.medimitr.ui.account.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.example.medimitr.domain.auth.User
import org.example.medimitr.ui.account.screenmodel.AccountScreenModel
import org.example.medimitr.ui.account.screenmodel.AccountUiState
import org.example.medimitr.ui.account.screenmodel.EditingField
import org.example.medimitr.ui.components.MediMitrTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountScreen() {
    val screenModel = koinViewModel<AccountScreenModel>()
    val state by screenModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success/error messages
    LaunchedEffect(state.updateError, state.updateSuccessMessage) {
        if (state.updateError != null) {
            snackbarHostState.showSnackbar(
                message = state.updateError!!,
                duration = SnackbarDuration.Short,
            )
            screenModel.clearMessages() // Clear message after showing
        }
        if (state.updateSuccessMessage != null) {
            snackbarHostState.showSnackbar(
                message = state.updateSuccessMessage!!,
                duration = SnackbarDuration.Short,
            )
            screenModel.clearMessages() // Clear message after showing
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { MediMitrTopAppBar(title = "My Account") },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.loadError != null ->
                    Text(
                        "Error: ${state.loadError}",
                        color = MaterialTheme.colorScheme.error,
                    )

                state.user == null -> Text("User data not available.")
                else ->
                    AccountDetails(
                        state = state,
                        user = state.user!!,
                        onStartEdit = screenModel::startEditing,
                        onSave = screenModel::saveField,
                        onCancel = screenModel::cancelEditing,
                        onChangePasswordRequest = { screenModel.showPasswordDialog(true) },
                        onLogout = { screenModel.logout() },
                    )
            }
        }
    }

    // Password Change Dialog
    if (state.showPasswordChangeDialog) {
        ChangePasswordDialog(
            isLoading = state.isUpdating,
            error = state.updateError,
            onConfirm = { old, new -> screenModel.changePassword(old, new) },
            onDismiss = { screenModel.showPasswordDialog(false) },
        )
    }
}

@Composable
fun AccountDetails(
    state: AccountUiState,
    user: User,
    onStartEdit: (EditingField) -> Unit,
    onSave: (EditingField, String) -> Unit,
    onCancel: () -> Unit,
    onChangePasswordRequest: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make content scrollable
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // User Name (Not editable in this example)
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "User",
            modifier = Modifier.size(80.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(user.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        // --- Editable Fields ---
        EditableUserInfoField(
            label = "Email",
            value = user.email,
            icon = Icons.Default.Email,
            isEditing = state.currentlyEditing == EditingField.EMAIL,
            isLoading = state.isUpdating && state.currentlyEditing == EditingField.EMAIL,
            keyboardType = KeyboardType.Email,
            onStartEdit = { onStartEdit(EditingField.EMAIL) },
            onSave = { newValue -> onSave(EditingField.EMAIL, newValue) },
            onCancel = onCancel,
        )

        EditableUserInfoField(
            label = "Phone",
            value = user.phone,
            icon = Icons.Default.Phone,
            isEditing = state.currentlyEditing == EditingField.PHONE,
            isLoading = state.isUpdating && state.currentlyEditing == EditingField.PHONE,
            keyboardType = KeyboardType.Phone,
            onStartEdit = { onStartEdit(EditingField.PHONE) },
            onSave = { newValue -> onSave(EditingField.PHONE, newValue) },
            onCancel = onCancel,
        )

        EditableUserInfoField(
            label = "Address",
            value = user.address,
            icon = Icons.Default.Home,
            isEditing = state.currentlyEditing == EditingField.ADDRESS,
            isLoading = state.isUpdating && state.currentlyEditing == EditingField.ADDRESS,
            keyboardType = KeyboardType.Text, // General text
            singleLine = false, // Address can be multiline
            maxLines = 4,
            onStartEdit = { onStartEdit(EditingField.ADDRESS) },
            onSave = { newValue -> onSave(EditingField.ADDRESS, newValue) },
            onCancel = onCancel,
        )

        Spacer(Modifier.height(16.dp))

        // --- Actions ---
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        TextButton(
            onClick = onChangePasswordRequest,
            enabled = !state.isUpdating, // Disable while any update is happening
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Change Password")
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onLogout,
            enabled = !state.isLoggingOut,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border =
                ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MaterialTheme.colorScheme.error), // Need brush in M3
                ),
        ) {
            if (state.isLoggingOut) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Logout")
            }
        }
    }
}

// Reusable Composable for displaying/editing user info fields
@Composable
fun EditableUserInfoField(
    label: String,
    value: String,
    icon: ImageVector,
    isEditing: Boolean,
    isLoading: Boolean, // Loading state specific to this field's save action
    keyboardType: KeyboardType,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    onStartEdit: () -> Unit,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    // Temporary state to hold input value while editing
    var editValue by remember(value, isEditing) { mutableStateOf(value) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Subtle elevation
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Text(label, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                if (!isEditing) {
                    IconButton(
                        onClick = onStartEdit,
                        enabled = !isLoading,
                    ) {
                        // Disable edit if another field is saving
                        Icon(Icons.Default.Edit, contentDescription = "Edit $label")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(visible = !isEditing) {
                Text(
                    value,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 40.dp),
                ) // Indent value slightly
            }

            AnimatedVisibility(visible = isEditing) {
                Column {
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("New $label") },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = keyboardType,
                                imeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
                            ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = singleLine,
                        maxLines = maxLines,
                        enabled = !isLoading,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                        }
                        TextButton(onClick = onCancel, enabled = !isLoading) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onSave(editValue)
                            },
                            enabled = editValue.isNotBlank() && editValue != value && !isLoading,
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

// Dialog for Changing Password
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    isLoading: Boolean,
    error: String?,
    onConfirm: (oldPass: String, newPass: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    LaunchedEffect(newPassword, confirmPassword) {
        passwordsMatch = newPassword == confirmPassword
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showOldPassword = !showOldPassword }) {
                            Icon(
                                if (showOldPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                "Toggle visibility",
                            )
                        }
                    },
                    isError = error != null, // Indicate error on fields? Maybe just below.
                    enabled = !isLoading,
                )
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                if (showNewPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                "Toggle visibility",
                            )
                        }
                    },
                    isError = !passwordsMatch && confirmPassword.isNotEmpty(),
                    enabled = !isLoading,
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                "Toggle visibility",
                            )
                        }
                    },
                    isError = !passwordsMatch && confirmPassword.isNotEmpty(),
                    enabled = !isLoading,
                )
                if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                    Text(
                        "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (error != null) { // Display general update error
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(oldPassword, newPassword) },
                enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty() && passwordsMatch && !isLoading,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Confirm")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancel")
            }
        },
    )
}
