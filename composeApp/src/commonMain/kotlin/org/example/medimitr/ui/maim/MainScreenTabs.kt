package org.example.medimitr.ui.maim

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.medimitr.ui.account.screen.AccountScreen
import org.example.medimitr.ui.home.HomeScreen
import org.example.medimitr.ui.order.cart.CartScreen
import org.example.medimitr.ui.order.orderhistory.OrderHistoryScreen

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 0u,
                title = "Home",
                icon = rememberVectorPainter(Icons.Default.Home),
            )

    @Composable
    override fun Content() {
        Navigator(HomeScreen())
    }
}

object CartTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 1u,
                title = "Cart",
                icon = rememberVectorPainter(Icons.Default.ShoppingCart),
            )

    @Composable
    override fun Content() {
        Navigator(CartScreen())
    }
}

object OrderTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 2u,
                title = "Orders",
                icon = rememberVectorPainter(Icons.AutoMirrored.Filled.ListAlt),
            )

    @Composable
    override fun Content() {
        Navigator(OrderHistoryScreen())
    }
}

object AccountTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 3u,
                title = "Account",
                icon = rememberVectorPainter(Icons.Default.AccountCircle),
            )

    @Composable
    override fun Content() {
        Navigator(AccountScreen())
    }
}
