package org.example.medimitr.ui.maim

import androidx.compose.material.icons.filled.Home
import kotlinx.serialization.Serializable
//
// object HomeTab : Tab {
//    override val options: TabOptions
//        @Composable
//        get() =
//            TabOptions(
//                index = 0u,
//                title = "Home",
//                icon = rememberVectorPainter(Icons.Default.Home),
//            )
//
//    @Composable
//    override fun Content() {
//        Navigator(HomeScreen())
//    }
// }
//
// object CartTab : Tab {
//    override val options: TabOptions
//        @Composable
//        get() =
//            TabOptions(
//                index = 1u,
//                title = "Cart",
//                icon = rememberVectorPainter(Icons.Default.ShoppingCart),
//            )
//
//    @Composable
//    override fun Content() {
//        Navigator(CartScreen())
//    }
// }
//
// object OrderHistoryTab : Tab {
//    override val options: TabOptions
//        @Composable
//        get() =
//            TabOptions(
//                index = 2u,
//                title = "History",
//                icon = rememberVectorPainter(Icons.Default.Menu),
//            )
//
//    @Composable
//    override fun Content() {
//        Navigator(OrderHistoryScreen())
//    }
// }
//
// object AccountTab : Tab {
//    override val options: TabOptions
//        @Composable
//        get() =
//            TabOptions(
//                index = 3u,
//                title = "Account",
//                icon = rememberVectorPainter(Icons.Default.AccountBox),
//            )
//
//    @Composable
//    override fun Content() {
//        Navigator(AccountScreen())
//    }
// }

sealed class MainScreenTabs {
    @Serializable
    object Home : MainScreenTabs()

    @Serializable
    object Cart : MainScreenTabs()

    @Serializable
    object OrderHistory : MainScreenTabs()

    @Serializable
    object Account : MainScreenTabs()

    companion object {
        val allTabsRoutes =
            listOf(
                Home::class.qualifiedName,
                Cart::class.qualifiedName,
                OrderHistory::class.qualifiedName,
                Account::class.qualifiedName,
            )
    }
}
