package org.example.medimitr.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.medimitr.common.formatReadableDate
import org.example.medimitr.common.formatText
import org.example.medimitr.domain.order.Order
import org.example.medimitr.ui.screenmodel.OrderHistoryScreenModel
import org.koin.mp.KoinPlatform.getKoin

class OrderHistoryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { getKoin().get<OrderHistoryScreenModel>() }
        // Use Voyager's Koin integration to get ScreenModel
        val state by screenModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Order History") })
                // Can add back navigation if this screen is pushed onto a stack
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator()
                    state.error != null ->
                        Text(
                            "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                        )

                    state.orders.isEmpty() -> Text("You haven't placed any orders yet.")
                    else ->
                        OrderHistoryList(state.orders) { orderId ->
                            navigator.push(OrderDetailScreen(orderId)) // Navigate to Detail Screen
                        }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryList(
    orders: List<Order>,
    onOrderClick: (orderId: Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(orders, key = { it.id }) { order ->
            OrderSummaryItem(order = order, onClick = { onOrderClick(order.id) })
        }
    }
}

@Composable
fun OrderSummaryItem(
    order: Order,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Placed on: ${formatReadableDate(order.datePlaced)}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Total: ₹${"%.2f".formatText(order.total)}", // Use INR symbol
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}
