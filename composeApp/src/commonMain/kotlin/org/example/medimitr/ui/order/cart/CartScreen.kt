package org.example.medimitr.ui.order.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.medimitr.common.formatToTwoDecimal
import org.example.medimitr.domain.cart.CartItem
import org.example.medimitr.ui.components.MediMitrTopAppBar
import org.koin.compose.viewmodel.koinViewModel

// ui/screen/CartScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onCheckout: (PriceDetails) -> Unit) {
    val screenModel = koinViewModel<CartScreenViewModel>()
    val state by screenModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true) // Bottom sheet state

    // Show errors in Snackbar
    LaunchedEffect(state.error) {
        if (state.error != null) {
            snackbarHostState.showSnackbar(
                message = state.error!!,
                duration = SnackbarDuration.Short,
            )
            screenModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { MediMitrTopAppBar(title = "Your Cart") },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.cartItems.isEmpty()) {
                EmptyCartView(modifier = Modifier.align(Alignment.Center))
            } else {
                CartContentView(
                    state = state,
                    onQuantityChange = screenModel::updateQuantity,
                    onShowPriceDetails = screenModel::showPriceDetailsSheet,
                    onCheckout = { screenModel.onProceedToCheckout(onCheckout) },
                )
            }
        }
    }

    // --- Remove Item Confirmation Dialog ---
    if (state.showRemoveConfirmationDialog && state.itemPendingRemoval != null) {
        AlertDialog(
            onDismissRequest = screenModel::cancelRemoveItem,
            title = { Text("Remove Item?") },
            text = { Text("Do you want to remove ${state.itemPendingRemoval?.medicine?.name} from your cart?") },
            confirmButton = {
                Button(onClick = screenModel::confirmRemoveItem) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = screenModel::cancelRemoveItem) { Text("Cancel") }
            },
        )
    }

    // --- Price Details Bottom Sheet ---
    if (state.showPriceDetailsSheet) {
        ModalBottomSheet(
            onDismissRequest = screenModel::hidePriceDetailsSheet,
            sheetState = sheetState,
            // You can customize shape, colors etc. here
        ) {
            PriceDetailsBottomSheetContent(details = state.priceDetails)
        }
    }
}

// --- Composable Sections ---

@Composable
fun EmptyCartView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Your cart is empty!", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Add items to get started.")
        // Optionally add a button to browse medicines
    }
}

@Composable
fun CartContentView(
    state: CartUiState,
    onQuantityChange: (medicineId: String, newQuantity: Int) -> Unit,
    onShowPriceDetails: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.cartItems, key = { it.medicine.id }) { item ->
                CartItemCard(
                    item = item,
                    onQuantityChange = { newQuantity ->
                        onQuantityChange(item.medicine.id, newQuantity)
                    },
                )
            }
        }
        // --- Summary Section ---
        CartSummarySection(
            details = state.priceDetails,
            isCartEmpty = state.cartItems.isEmpty(),
            onShowPriceDetails = onShowPriceDetails,
            onCheckout = onCheckout,
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (newQuantity: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Image
//            AsyncImage(
//                model = item.medicine.imageUrl ?: "", // Provide a fallback drawable if needed
//                contentDescription = item.medicine.name,
//                modifier = Modifier.size(72.dp).padding(end = 12.dp),
//                contentScale = ContentScale.Crop,
//                // Placeholder/Error can be added here
//                // placeholder = painterResource(...)
//            )

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(item.medicine.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "₹${item.medicine.price.formatToTwoDecimal()}", // Price per item
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Quantity Control
            QuantitySelector(
                quantity = item.quantity,
                onQuantityChange = onQuantityChange,
            )
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (newQuantity: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Decrease Button
        IconButton(
            onClick = { onQuantityChange(quantity - 1) },
            modifier = Modifier.size(32.dp),
            // Enable logic handled in ScreenModel (triggers confirmation if qty becomes 0)
        ) {
            Icon(
                imageVector = if (quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                contentDescription = if (quantity == 1) "Remove item" else "Decrease quantity",
                tint = if (quantity == 1) MaterialTheme.colorScheme.error else LocalContentColor.current,
            )
        }

        // Quantity Text
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.widthIn(min = 24.dp), // Ensure minimum width
            textAlign = TextAlign.Center,
        )

        // Increase Button
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase quantity")
        }
    }
}

@Composable
fun CartSummarySection(
    details: PriceDetails,
    isCartEmpty: Boolean,
    onShowPriceDetails: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        // Use Surface for elevation/background separation
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp, // Add shadow to lift it visually
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onShowPriceDetails),
                // Make row clickable
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Total Amount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    Icons.Default.Info, // Info icon to suggest clickability
                    contentDescription = "Show price details",
                    modifier = Modifier.size(18.dp).padding(start = 4.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "₹${details.total.formatToTwoDecimal()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onCheckout,
                enabled = !isCartEmpty,
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@Composable
fun PriceDetailsBottomSheetContent(
    details: PriceDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            "Price Details",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        PriceDetailRow("Subtotal", details.subtotal)
        PriceDetailRow("Delivery Charge", details.deliveryCharge)
        if (details.discount > 0) {
            PriceDetailRow("Discount", details.discount, isDiscount = true)
        }
        PriceDetailRow("Platform Fee", details.platformFee)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Total Amount",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                "₹${details.total.formatToTwoDecimal()}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun PriceDetailRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${if (isDiscount) "- " else ""}₹${amount.formatToTwoDecimal()}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDiscount) Color(0xFF008000) else LocalContentColor.current, // Green for discount
        )
    }
}
