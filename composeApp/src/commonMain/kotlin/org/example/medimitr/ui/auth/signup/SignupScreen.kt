package org.example.medimitr.ui.auth.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

// ui/screen/SignupScreen.kt
@Composable
fun SignupScreen(
    onBack: () -> Unit, // Assuming you have a Navigator interface for navigation
    onSignUpSuccess: () -> Unit, // Callback for sign-in action
) {
    val viewModel = koinViewModel<SignupScreenViewModel>()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        TextField(
            value = viewModel.phone,
            onValueChange = { viewModel.phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = viewModel.address,
            onValueChange = { viewModel.address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Spacer(Modifier.height(16.dp))
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }
        viewModel.errorMessage?.let {
            Text(it, color = Color.Red)
        }
        Button(
            onClick = { viewModel.onSignupClick(onSignUpSuccess) },
            enabled = !viewModel.isLoading,
        ) {
            Text("Sign Up")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Already have an account? Login",
            modifier = Modifier.clickable { onBack() },
        )
    }
}
