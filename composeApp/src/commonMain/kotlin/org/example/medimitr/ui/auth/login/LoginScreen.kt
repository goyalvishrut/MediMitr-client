package org.example.medimitr.ui.auth.login

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/LoginScreen.kt
@Composable
fun LoginScreen(onSignUpClicked: () -> Unit) {
    val screenModel = remember { getKoin().get<LoginScreenModel>() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = screenModel.email,
            onValueChange = { screenModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = screenModel.password,
            onValueChange = { screenModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(16.dp))
        if (screenModel.isLoading) {
            CircularProgressIndicator()
        }
        screenModel.errorMessage?.let {
            Text(it, color = Color.Red)
        }
        Button(
            onClick = { screenModel.onLoginClick() },
            enabled = !screenModel.isLoading,
        ) {
            Text("Login")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Don't have an account? Sign up",
            modifier = Modifier.clickable { onSignUpClicked() },
        )
    }
}
