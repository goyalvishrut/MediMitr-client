package org.example.medimitr.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.flow.map
import org.example.medimitr.domain.cart.CartRepository
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/MainScreen.kt
class MainScreen : Screen {
    @Composable
    override fun Content() {
        val cartRepository = getKoin().get<CartRepository>()
        val cartItemCount by cartRepository.cartItems.map { it.size }.collectAsState(initial = 0)

        TabNavigator(HomeTab) { tabNavigator ->
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(tab = HomeTab)
                        TabNavigationItem(tab = CartTab, badge = cartItemCount)
                        TabNavigationItem(tab = OrderHistoryTab)
                        TabNavigationItem(tab = AccountTab)
                    }
                },
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun TabNavigationItem(
    tab: Tab,
    badge: Int = 0,
) {
    val tabNavigator = LocalTabNavigator.current
    NavigationRailItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            BadgedBox(badge = {
                if (badge > 0) Badge { Text(badge.toString()) }
            }) {
                Icon(tab.options.icon!!, contentDescription = tab.options.title)
            }
        },
        label = { Text(tab.options.title) },
    )
}
