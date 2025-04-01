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
import cafe.adriel.voyager.navigator.Navigator
import org.example.medimitr.domain.auth.AuthStatus
import org.example.medimitr.domain.auth.UserRepository
import org.example.medimitr.ui.auth.AuthCheckScreen
import org.example.medimitr.ui.maim.MainScreen
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AppNavigation() {
    val userRepository: UserRepository = getKoin().get<UserRepository>()
    val authStatus by userRepository.observeAuthStatus.collectAsState()

    MaterialTheme {
        when (authStatus) {
            AuthStatus.UNKNOWN -> {
                // Optional: Show a loading indicator while checking auth status
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    userRepository.updateAuthStatus()
                }
            }

            AuthStatus.LOGGED_IN -> {
                // User is logged in, show the main app UI with bottom navigation
                // This composable likely contains its own nested Voyager Navigator
                Navigator(MainScreen())
            }

            AuthStatus.LOGGED_OUT -> {
                // User is logged out, show the authentication flow screens
                // This uses a separate Voyager Navigator instance for the auth flow
                Navigator(AuthCheckScreen()) // Start auth flow
            }
        }
    }
}
