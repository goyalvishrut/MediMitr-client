package org.example.medimitr.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.medimitr.domain.auth.AuthStatus
import org.example.medimitr.domain.auth.UserRepository
import org.example.medimitr.ui.main.navigation.AuthorisedFlowScreen
import org.example.medimitr.ui.main.navigation.LoginFlowScreen
import org.example.medimitr.ui.main.navigation.Screen
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AppNavigation() {
    val userRepository: UserRepository = getKoin().get<UserRepository>()
    val authStatus by userRepository.observeAuthStatus.collectAsState()

    val navController = rememberNavController()
    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = getStartDestination(authStatus),
        ) {
            composable<Screen.Auth> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    userRepository.updateAuthStatus()
                }
            }
            composable<Screen.LoginFlow> {
                LoginFlowScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.AuthorisedFlow) {
                            popUpTo(Screen.LoginFlow) { inclusive = true }
                        }
                    },
                )
            }
            composable<Screen.AuthorisedFlow> {
                AuthorisedFlowScreen()
            }
        }
    }
}

fun getStartDestination(authStatus: AuthStatus): Screen =
    when (authStatus) {
        AuthStatus.UNKNOWN -> Screen.Auth
        AuthStatus.LOGGED_IN -> Screen.AuthorisedFlow
        AuthStatus.LOGGED_OUT -> Screen.LoginFlow
    }
