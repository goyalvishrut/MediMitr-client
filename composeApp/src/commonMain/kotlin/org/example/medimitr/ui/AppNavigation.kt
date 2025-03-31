package org.example.medimitr.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.example.medimitr.ui.screens.AuthCheckScreen

@Composable
fun AppNavigation() {
    MaterialTheme {
        Navigator(screen = AuthCheckScreen())
    }
}
