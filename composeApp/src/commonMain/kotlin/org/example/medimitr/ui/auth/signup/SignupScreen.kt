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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/SignupScreen.kt
class SignupScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { getKoin().get<SignupScreenModel>() }
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TextField(
                value = screenModel.name,
                onValueChange = { screenModel.name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))
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
            TextField(
                value = screenModel.phone,
                onValueChange = { screenModel.phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = screenModel.address,
                onValueChange = { screenModel.address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.height(16.dp))
            if (screenModel.isLoading) {
                CircularProgressIndicator()
            }
            screenModel.errorMessage?.let {
                Text(it, color = Color.Red)
            }
            Button(
                onClick = { screenModel.onSignupClick(navigator) },
                enabled = !screenModel.isLoading,
            ) {
                Text("Sign Up")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Already have an account? Login",
                modifier = Modifier.clickable { navigator.pop() },
            )
        }
    }
}
