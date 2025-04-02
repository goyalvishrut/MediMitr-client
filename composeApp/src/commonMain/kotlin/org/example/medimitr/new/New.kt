package org.example.medimitr.new

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Top-level screens
sealed class Screen(
    val route: String,
) {
    object Auth : Screen("auth")

    object LoginFlow : Screen("login_flow")

    object HomeFlow : Screen("home_flow")
}

// Screens for the HomeFlow (the tabs)
sealed class HomeFlowScreen(
    val route: String,
) {
    object HomeTab : HomeFlowScreen("home_tab")

    object CartTab : HomeFlowScreen("cart_tab")

    object OrdersTab : HomeFlowScreen("orders_tab")

    object AccountTab : HomeFlowScreen("account_tab")
}

// Screens for HomeTab flow
sealed class HomeTabFlowScreen(
    val route: String,
) {
    object SearchScreen : HomeTabFlowScreen("search_screen")

    object MedicineDetails : HomeTabFlowScreen("medicine_details")
}

// Screens for CartTab flow
sealed class CartTabFlowScreen(
    val route: String,
) {
    object CheckoutScreen : CartTabFlowScreen("checkout_screen")

    object OrderConfirmation : CartTabFlowScreen("order_confirmation")
}

// Screens for OrdersTab flow
sealed class OrdersTabFlowScreen(
    val route: String,
) {
    object OrderList : OrdersTabFlowScreen("order_list")

    object OrderDetails : OrdersTabFlowScreen("order_details")
}

// Screens for AccountTab flow (empty for now, but keeping it modular)
sealed class AccountTabFlowScreen(
    val route: String,
) {
    // Add screens here if needed
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) } // Simulate auth state

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Screen.HomeFlow.route else Screen.Auth.route,
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    isAuthenticated = true
                    navController.navigate(Screen.HomeFlow.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.LoginFlow.route) {
            LoginFlowScreen(
                onLoginSuccess = {
                    isAuthenticated = true
                    navController.navigate(Screen.HomeFlow.route) {
                        popUpTo(Screen.LoginFlow.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.HomeFlow.route) {
            HomeFlowScreen()
        }
    }
}

@Composable
fun HomeFlowScreen() {
    val navController = rememberNavController()
    val currentTabRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    // State to track the current nested route within the active tab
    var currentNestedRoute by remember { mutableStateOf("home_tab_content") }

    // List of routes where the bottom bar should be visible (the main tab content screens)
    val tabContentRoutes =
        listOf(
            "home_tab_content",
            "cart_tab_content",
            "orders_tab_content",
            "account_tab_content",
        )

    // Show bottom bar only if the current nested route is a tab content route
    val showBottomBar = currentNestedRoute in tabContentRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeFlowScreen.HomeTab.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(HomeFlowScreen.HomeTab.route) {
                HomeTabFlow(
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
            composable(HomeFlowScreen.CartTab.route) {
                CartTabFlow(
                    parentNavController = navController,
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
            composable(HomeFlowScreen.OrdersTab.route) {
                OrdersTabFlow(
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
            composable(HomeFlowScreen.AccountTab.route) {
                AccountTabFlow(
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == HomeFlowScreen.HomeTab.route,
            onClick = {
                navController.navigate(HomeFlowScreen.HomeTab.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = currentRoute == HomeFlowScreen.CartTab.route,
            onClick = {
                navController.navigate(HomeFlowScreen.CartTab.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Orders") },
            label = { Text("Orders") },
            selected = currentRoute == HomeFlowScreen.OrdersTab.route,
            onClick = {
                navController.navigate(HomeFlowScreen.OrdersTab.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            label = { Text("Account") },
            selected = currentRoute == HomeFlowScreen.AccountTab.route,
            onClick = {
                navController.navigate(HomeFlowScreen.AccountTab.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
    }
}

@Composable
fun HomeTabFlow(
    onCurrentScreenChanged: (String) -> Unit, // Callback to report the current screen
) {
    val navController = rememberNavController()
    val currentRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    // Report the current route to the parent
    currentRoute?.let { onCurrentScreenChanged(it) }

    NavHost(
        navController = navController,
        startDestination = "home_tab_content",
    ) {
        composable("home_tab_content") {
            HomeTabScreen(
                onSearchClick = { navController.navigate(HomeTabFlowScreen.SearchScreen.route) },
            )
        }
        composable(HomeTabFlowScreen.SearchScreen.route) {
            SearchScreen(
                onMedicineClick = { navController.navigate(HomeTabFlowScreen.MedicineDetails.route) },
            )
        }
        composable(HomeTabFlowScreen.MedicineDetails.route) {
            MedicineDetailsScreen()
        }
    }
}

@Composable
fun CartTabFlow(
    parentNavController: NavHostController,
    onCurrentScreenChanged: (String) -> Unit,
) {
    val navController = rememberNavController()
    val currentRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    currentRoute?.let { onCurrentScreenChanged(it) }

    NavHost(
        navController = navController,
        startDestination = "cart_tab_content",
    ) {
        composable("cart_tab_content") {
            CartTabScreen(
                onCheckoutClick = { navController.navigate(CartTabFlowScreen.CheckoutScreen.route) },
            )
        }
        composable(CartTabFlowScreen.CheckoutScreen.route) {
            CheckoutScreen(
                onConfirmClick = { navController.navigate(CartTabFlowScreen.OrderConfirmation.route) },
            )
        }
        composable(CartTabFlowScreen.OrderConfirmation.route) {
            OrderConfirmationScreen(
                onGoToHome = {
                    parentNavController.navigate(HomeFlowScreen.HomeTab.route) {
                        popUpTo(HomeFlowScreen.CartTab.route) { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
fun OrdersTabFlow(onCurrentScreenChanged: (String) -> Unit) {
    val navController = rememberNavController()
    val currentRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    currentRoute?.let { onCurrentScreenChanged(it) }

    NavHost(
        navController = navController,
        startDestination = "orders_tab_content",
    ) {
        composable("orders_tab_content") {
            OrdersTabScreen(
                onOrderListClick = { navController.navigate(OrdersTabFlowScreen.OrderList.route) },
            )
        }
        composable(OrdersTabFlowScreen.OrderList.route) {
            OrderListScreen(
                onOrderClick = { navController.navigate(OrdersTabFlowScreen.OrderDetails.route) },
            )
        }
        composable(OrdersTabFlowScreen.OrderDetails.route) {
            OrderDetailsScreen()
        }
    }
}

@Composable
fun AccountTabFlow(onCurrentScreenChanged: (String) -> Unit) {
    val navController = rememberNavController()
    val currentRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    currentRoute?.let { onCurrentScreenChanged(it) }

    NavHost(
        navController = navController,
        startDestination = "account_tab_content",
    ) {
        composable("account_tab_content") {
            AccountTabScreen()
        }
    }
}

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onAuthSuccess) {
            Text("Authenticate")
        }
    }
}

@Composable
fun LoginFlowScreen(onLoginSuccess: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onLoginSuccess) {
            Text("Login")
        }
    }
}

@Composable
fun HomeTabScreen(onSearchClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onSearchClick) {
            Text("Go to Search")
        }
    }
}

@Composable
fun CartTabScreen(onCheckoutClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onCheckoutClick) {
            Text("Go to Checkout")
        }
    }
}

@Composable
fun OrdersTabScreen(onOrderListClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onOrderListClick) {
            Text("View Orders")
        }
    }
}

@Composable
fun AccountTabScreen() {
    Box(modifier = Modifier.padding(16.dp)) {
        Text("Account Tab")
    }
}

@Composable
fun SearchScreen(onMedicineClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onMedicineClick) {
            Text("View Medicine Details")
        }
    }
}

@Composable
fun MedicineDetailsScreen() {
    Box(modifier = Modifier.padding(16.dp)) {
        Text("Medicine Details")
    }
}

@Composable
fun CheckoutScreen(onConfirmClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onConfirmClick) {
            Text("Confirm Order")
        }
    }
}

@Composable
fun OrderConfirmationScreen(onGoToHome: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onGoToHome) {
            Text("Go to Home")
        }
    }
}

@Composable
fun OrderListScreen(onOrderClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onOrderClick) {
            Text("View Order Details")
        }
    }
}

@Composable
fun OrderDetailsScreen() {
    Box(modifier = Modifier.padding(16.dp)) {
        Text("Order Details")
    }
}
