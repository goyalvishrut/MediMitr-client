package org.example.medimitr.ui.screens.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.medimitr.common.formatReadableDate
import org.example.medimitr.common.formatText
import org.example.medimitr.domain.cart.CartItem
import org.example.medimitr.domain.order.Order
import org.example.medimitr.ui.screenmodel.OrderDetailScreenModel
import org.koin.mp.KoinPlatform.getKoin

// Screen requires orderId to know which order to display
data class OrderDetailScreen(
    val orderId: Int,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { getKoin().get<OrderDetailScreenModel>() }
        val state by screenModel.uiState.collectAsState()

        // Load details when the screen is shown or orderId changes
        LaunchedEffect(orderId) {
            screenModel.loadOrderDetails(orderId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Order Details #$orderId") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            // Back navigation
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
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

                    state.order == null -> Text("Order details not available.")
                    else -> OrderDetailsContent(state.order!!) // Show details if order is loaded
                }
            }
        }
    }
}

@Composable
fun OrderDetailsContent(order: Order) {
    LazyColumn(
        // Use LazyColumn in case of many items or long address
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Order Summary Section
        item {
            OrderInfoSection(order)
            Divider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // Items Header
        item {
            Text("Items in this Order", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        // Order Items List
        items(
            order.items,
            key = { it.medicine.id },
        ) { item ->
            // Assuming medicineId is unique within an order
            OrderItemRow(item = item)
        }

        // Total Amount Footer
        item {
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Total Amount: ₹${"%.2f".formatText(order.total)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End),
            )
        }
    }
}

@Composable
fun OrderInfoSection(order: Order) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Order ID: ${order.id}")
        Text("Placed on: ${formatReadableDate(order.datePlaced)}")
        Text("Status: ${order.status}")
        Text("Delivery Address: ${order.status}")
        Text("Contact Phone: ${order.status}")
    }
}

@Composable
fun OrderItemRow(item: CartItem) {
    // Assuming OrderItem now contains medicineName
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.medicine.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Qty: ${item.quantity} @ ₹${"%.2f".formatText(item.medicine.price)} each",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(
            text = "₹${"%.2f".formatText(item.quantity * item.medicine.price)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
