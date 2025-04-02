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
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.example.medimitr.common.serializableType
import org.example.medimitr.ui.account.screen.AccountScreen
import org.example.medimitr.ui.auth.login.LoginScreen
import org.example.medimitr.ui.auth.signup.SignupScreen
import org.example.medimitr.ui.home.HomeScreen
import org.example.medimitr.ui.medicine.MedicineDetailScreen
import org.example.medimitr.ui.order.cart.CartScreen
import org.example.medimitr.ui.order.cart.PriceDetails
import org.example.medimitr.ui.order.checkout.CheckoutScreen
import org.example.medimitr.ui.order.orderconfirmation.OrderPlacedScreen
import org.example.medimitr.ui.order.orderhistory.OrderDetailScreen
import org.example.medimitr.ui.order.orderhistory.OrderHistoryScreen
import org.example.medimitr.ui.search.SearchScreen
import kotlin.reflect.typeOf

// Top-level screens
@Serializable
sealed class Screen {
    @Serializable
    data object Auth : Screen()

    @Serializable
    data object LoginFlow : Screen()

    @Serializable
    data object AuthorisedFlow : Screen()
}

// Screens for the HomeFlow (the tabs)
@Serializable
sealed class AuthorisedFlowScreen(
    val route: String,
) {
    @Serializable
    data object HomeTab : AuthorisedFlowScreen("home_tab")

    @Serializable
    data object CartTab : AuthorisedFlowScreen("cart_tab")

    @Serializable
    data object OrdersTab : AuthorisedFlowScreen("orders_tab")

    @Serializable
    data object AccountTab : AuthorisedFlowScreen("account_tab")
}

// Screens for HomeTab flow
@Serializable
sealed class HomeTabFlowScreen {
    @Serializable
    data object SearchScreen : HomeTabFlowScreen()

    @Serializable
    data class MedicineDetails(
        val medicineId: String,
    ) : HomeTabFlowScreen()
}

// Screens for CartTab flow
@Serializable
sealed class CartTabFlowScreen {
    @Serializable
    data class CheckoutScreen(
        val priceDetails: PriceDetails,
    ) : CartTabFlowScreen()

    @Serializable
    data class OrderConfirmation(
        val orderId: String,
    ) : CartTabFlowScreen()
}

// Screens for OrdersTab flow
@Serializable
sealed class OrdersTabFlowScreen {
    @Serializable
    data class OrderDetails(
        val orderId: Int,
    ) : OrdersTabFlowScreen()
}

// Screens for AccountTab flow (empty for now, but keeping it modular)
@Serializable
sealed class AccountTabFlowScreen {
    // Add screens here if needed
}

@Serializable
sealed class LoginFlowScreen {
    @Serializable
    data object Login : LoginFlowScreen()

    @Serializable
    data object SignUp : LoginFlowScreen()
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) } // Simulate auth state

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Screen.AuthorisedFlow else Screen.Auth,
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                onAuthSuccess = {
                    isAuthenticated = true
                    navController.navigate(Screen.AuthorisedFlow) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.LoginFlow> {
            LoginFlowScreen(
                onLoginSuccess = {
                    isAuthenticated = true
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

@Composable
fun AuthorisedFlowScreen() {
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
            startDestination = AuthorisedFlowScreen.HomeTab,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<AuthorisedFlowScreen.HomeTab> {
                HomeTabFlow(
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
            composable<AuthorisedFlowScreen.CartTab> {
                CartTabFlow(
                    parentNavController = navController,
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
            composable<AuthorisedFlowScreen.OrdersTab> {
                OrdersTabFlow(
                    onCurrentScreenChanged = { route ->
                        currentNestedRoute = route
                    },
                )
            }
            composable<AuthorisedFlowScreen.AccountTab> {
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
            selected = currentRoute == AuthorisedFlowScreen.HomeTab.route,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.HomeTab) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = currentRoute == AuthorisedFlowScreen.CartTab.route,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.CartTab) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Orders") },
            label = { Text("Orders") },
            selected = currentRoute == AuthorisedFlowScreen.OrdersTab.route,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.OrdersTab) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            label = { Text("Account") },
            selected = currentRoute == AuthorisedFlowScreen.AccountTab.route,
            onClick = {
                navController.navigate(AuthorisedFlowScreen.AccountTab) {
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
            HomeScreen(
                onMedicineClick = { navController.navigate(HomeTabFlowScreen.MedicineDetails(it)) },
                onSearchClick = { navController.navigate(HomeTabFlowScreen.SearchScreen) },
                onCartClick = { },
            )
        }
        composable<HomeTabFlowScreen.SearchScreen> {
            SearchScreen(
                onMedicineClick = { navController.navigate(HomeTabFlowScreen.MedicineDetails(it)) },
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<HomeTabFlowScreen.MedicineDetails> {
            val args = it.toRoute<HomeTabFlowScreen.MedicineDetails>()
            MedicineDetailScreen(medicineId = args.medicineId) {
                navController.popBackStack()
            }
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
            CartScreen(
                onCheckout = { navController.navigate(CartTabFlowScreen.CheckoutScreen(priceDetails = it)) },
            )
        }
        composable<CartTabFlowScreen.CheckoutScreen>(typeMap = mapOf(typeOf<PriceDetails>() to serializableType<PriceDetails>())) {
            val args = it.toRoute<CartTabFlowScreen.CheckoutScreen>()
            CheckoutScreen(
                priceDetails = args.priceDetails, // Replace with actual price details
                onBack = { navController.popBackStack() },
                onOrderPlaced = { orderId ->
                    navController.navigate(CartTabFlowScreen.OrderConfirmation(orderId = orderId))
                },
            )
        }
        composable<CartTabFlowScreen.OrderConfirmation> {
            val args = it.toRoute<CartTabFlowScreen.OrderConfirmation>()
            OrderPlacedScreen(
                orderId = args.orderId,
                onGoToHome = {
                    parentNavController.navigate(AuthorisedFlowScreen.HomeTab) {
                        popUpTo(AuthorisedFlowScreen.CartTab) { inclusive = true }
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
            OrderHistoryScreen(
                onOrderClick = { navController.navigate(OrdersTabFlowScreen.OrderDetails(orderId = it)) },
            )
        }
        composable<OrdersTabFlowScreen.OrderDetails> {
            val args = it.toRoute<OrdersTabFlowScreen.OrderDetails>()
            OrderDetailScreen(args.orderId) {
                navController.popBackStack()
            }
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
            AccountScreen()
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
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginFlowScreen.Login,
    ) {
        composable<LoginFlowScreen.Login> {
            LoginScreen(
                onSignUpClicked = {
                    navController.navigate(LoginFlowScreen.SignUp)
                },
            )
        }
        composable<LoginFlowScreen.SignUp> {
            SignupScreen(
                onBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(LoginFlowScreen.Login) {
                        popUpTo(LoginFlowScreen.SignUp) { inclusive = true }
                    }
                },
            )
        }
    }
}
