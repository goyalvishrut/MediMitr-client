package org.example.medimitr.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.medimitr.ui.screens.account.AccountScreen
import org.example.medimitr.ui.screens.order.CartScreen
import org.example.medimitr.ui.screens.order.OrderHistoryScreen
import org.example.medimitr.ui.screens.search.SearchScreen

object SearchTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 0u,
                title = "Search",
                icon = rememberVectorPainter(Icons.Default.Search),
            )

    @Composable
    override fun Content() {
        Navigator(SearchScreen())
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

object OrderHistoryTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 2u,
                title = "History",
                icon = rememberVectorPainter(Icons.Default.Menu),
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
                icon = rememberVectorPainter(Icons.Default.AccountBox),
            )

    @Composable
    override fun Content() {
        Navigator(AccountScreen())
    }
}

object ContactTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 4u,
                title = "Contact",
                icon = rememberVectorPainter(Icons.Default.Email),
            )

    @Composable
    override fun Content() {
        Navigator(CartScreen())
    }
}
