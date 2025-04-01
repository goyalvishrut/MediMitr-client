package org.example.medimitr.ui.maim

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.map
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.ui.account.screen.AccountScreen
import org.example.medimitr.ui.home.HomeScreen
import org.example.medimitr.ui.maim.MainScreenTabs.Account
import org.example.medimitr.ui.maim.MainScreenTabs.Cart
import org.example.medimitr.ui.maim.MainScreenTabs.Home
import org.example.medimitr.ui.maim.MainScreenTabs.OrderHistory
import org.example.medimitr.ui.order.cart.CartScreen
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

    val bottomBarRoutes = MainScreenTabs.allTabsRoutes

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
            startDestination = Home,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<Home> { Navigator(HomeScreen()) }
            composable<Cart> { Navigator(CartScreen()) }
            composable<OrderHistory> { Navigator(OrderHistoryScreen()) }
            composable<Account> { Navigator(AccountScreen()) }
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
            selected = currentRoute == Home::class.qualifiedName,
            onClick = {
                navController.navigate(Home) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = {
                BadgedBox(badge = {
                    if (badge > 0) Badge { Text(badge.toString()) }
                }) {
                    Icon(Icons.Default.ShoppingCart, "Cart")
                }
            },
            label = { Text("Cart") },
            selected = currentRoute == Cart::class.qualifiedName,
            onClick = {
                navController.navigate(Cart) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, "History") },
            label = { Text("History") },
            selected = currentRoute == OrderHistory::class.qualifiedName,
            onClick = {
                navController.navigate(OrderHistory) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBox, "Account") },
            label = { Text("Account") },
            selected = currentRoute == Account::class.qualifiedName,
            onClick = {
                navController.navigate(Account) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
    }
}
