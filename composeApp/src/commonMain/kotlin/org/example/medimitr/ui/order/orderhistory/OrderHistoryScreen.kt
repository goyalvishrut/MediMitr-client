package org.example.medimitr.ui.order.orderhistory

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.medimitr.common.formatReadableDate
import org.example.medimitr.common.formatToTwoDecimal
import org.example.medimitr.domain.order.Order
import org.example.medimitr.ui.components.MediMitrTopAppBar
import org.koin.mp.KoinPlatform.getKoin

class OrderHistoryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { getKoin().get<OrderHistoryScreenModel>() }
        val state by screenModel.uiState.collectAsState()

        Scaffold(
            topBar = { MediMitrTopAppBar("Order History") },
            containerColor = Color(0xFFF5F7FA), // Light gray background
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                        )
                    }

                    state.error != null -> {
                        Text(
                            "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                        )
                    }

                    state.orders.isEmpty() -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "No Orders Yet",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Start shopping to see your orders here!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                            )
                        }
                    }

                    else -> {
                        OrderHistoryList(state.orders) { orderId ->
                            navigator.push(OrderDetailScreen(orderId))
                        }
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .animateContentSize(),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                            ),
                    ).padding(16.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Order #${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        "₹${order.total.formatToTwoDecimal()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Placed: ${formatReadableDate(order.datePlaced)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                    Text(
                        order.status,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color =
                            when (order.status.lowercase()) {
                                "delivered" -> Color(0xFF2ECC71)
                                "pending" -> Color(0xFFFFA500)
                                "cancelled" -> Color(0xFFE74C3C)
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                    )
                }
            }
        }
    }
}
