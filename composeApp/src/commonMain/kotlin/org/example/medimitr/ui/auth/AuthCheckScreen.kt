package org.example.medimitr.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.medimitr.domain.auth.AuthRepository
import org.example.medimitr.ui.auth.login.LoginScreen
import org.example.medimitr.ui.maim.MainScreen
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/AuthCheckScreen.kt
class AuthCheckScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authRepository = getKoin().get<AuthRepository>()

        LaunchedEffect(Unit) {
            if (authRepository.isLoggedIn()) {
                navigator.replaceAll(MainScreen())
            } else {
                navigator.replaceAll(LoginScreen())
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
