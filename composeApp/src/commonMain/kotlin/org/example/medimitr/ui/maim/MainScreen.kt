package org.example.medimitr.ui.maim

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.map
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.new.AuthorisedFlowScreen
import org.example.medimitr.ui.account.screen.AccountScreen
import org.example.medimitr.ui.home.HomeScreen
import org.example.medimitr.ui.order.orderhistory.OrderHistoryScreen
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/MainScreen.kt
@Composable
fun MainScreen() {
    val cartRepository = getKoin().get<CartRepository>()
    val cartItemCount by cartRepository.cartItems.map { it.size }.collectAsState(initial = 0)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes =
        listOf(
            AuthorisedFlowScreen.HomeTab::class.qualifiedName,
            AuthorisedFlowScreen.OrdersTab::class.qualifiedName,
            AuthorisedFlowScreen.AccountTab::class.qualifiedName,
        )

    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController, cartItemCount)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AuthorisedFlowScreen.HomeTab,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<AuthorisedFlowScreen.HomeTab> { HomeScreen({}, {}, {}) }
            composable<AuthorisedFlowScreen.OrdersTab> { OrderHistoryScreen({}) }
            composable<AuthorisedFlowScreen.AccountTab> { AccountScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    badge: Int = 0,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") },
            selected = currentRoute == AuthorisedFlowScreen.HomeTab::class.qualifiedName,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.HomeTab) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, "History") },
            label = { Text("History") },
            selected = currentRoute == AuthorisedFlowScreen.OrdersTab::class.qualifiedName,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.OrdersTab) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBox, "Account") },
            label = { Text("Account") },
            selected = currentRoute == AuthorisedFlowScreen.AccountTab::class.qualifiedName,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.AccountTab) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
    }
}
