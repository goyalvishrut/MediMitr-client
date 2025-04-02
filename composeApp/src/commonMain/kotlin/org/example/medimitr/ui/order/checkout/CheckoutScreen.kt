package org.example.medimitr.ui.order.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.example.medimitr.common.formatToTwoDecimal
import org.example.medimitr.ui.components.MediMitrTopAppBar
import org.example.medimitr.ui.order.cart.PriceDetailRow
import org.example.medimitr.ui.order.cart.PriceDetails
import org.koin.compose.viewmodel.koinViewModel

// ui/screen/CheckoutScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    priceDetails: PriceDetails,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit,
) {
    val viewModel = koinViewModel<CheckoutScreenViewModel>()

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.start(priceDetails)

    // Show placing order errors
    LaunchedEffect(state.placeOrderError) {
        if (state.placeOrderError != null) {
            snackbarHostState.showSnackbar(
                message = state.placeOrderError!!,
                duration = SnackbarDuration.Short,
            )
            viewModel.clearPlaceOrderError() // Clear error after showing
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediMitrTopAppBar(
                title = "Checkout",
                showBackButton = true,
            ) {
                onBack() // Pop the current screen
            }
        },
        // Bottom bar for placing order - stays fixed at the bottom
        bottomBar = {
            CheckoutActionBar(
                totalAmount = state.priceDetails.total,
                isLoading = state.isPlacingOrder,
                enabled = state.deliveryAddress.isNotBlank() && !state.isLoadingAddress,
                onPlaceOrder = { viewModel.onPlaceOrder(onOrderPlaced) },
            )
        },
    ) { paddingValues ->
        // Main content area - Make it scrollable
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply scaffold padding
                    .verticalScroll(rememberScrollState()) // Make content scrollable
                    .padding(bottom = 80.dp), // Add padding to prevent overlap with bottom bar
        ) {
            // Address Section
            AddressSection(
                isLoading = state.isLoadingAddress,
                address = state.deliveryAddress,
                isEditing = state.isEditingAddress,
                error = state.addressError,
                onEditToggle = { viewModel.startEditingAddress() },
                onSave = { newAddress -> viewModel.updateSelectedAddress(newAddress) }, // Just updates state
                onCancel = { viewModel.cancelEditingAddress() },
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Price Summary Section
            PriceSummarySection(details = state.priceDetails)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Payment Method Section
            PaymentMethodSection(method = state.paymentMethod)

            Spacer(Modifier.height(16.dp)) // Space before end of scroll
        }
    }
}

// --- Composable Sections ---

@Composable
fun AddressSection(
    isLoading: Boolean,
    address: String,
    isEditing: Boolean,
    error: String?,
    onEditToggle: () -> Unit,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var editedAddress by remember(address, isEditing) { mutableStateOf(address) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Delivery Address", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Loading address...", style = MaterialTheme.typography.bodyMedium)
            }
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!isEditing) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = "Address Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = address.ifBlank { "No address set" },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = onEditToggle, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Change Address")
                            }
                        }
                    } else {
                        // Editing UI
                        OutlinedTextField(
                            value = editedAddress,
                            onValueChange = { editedAddress = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Enter Delivery Address") },
                            keyboardOptions =
                                KeyboardOptions.Default.copy(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done,
                                ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = false,
                            maxLines = 4,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onCancel) { Text("Cancel") }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    onSave(editedAddress)
                                },
                                enabled = editedAddress.isNotBlank() && editedAddress != address,
                            ) {
                                Text("Use this Address")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceSummarySection(details: PriceDetails) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Bill Summary", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        PriceDetailRow("Subtotal", details.subtotal)
        PriceDetailRow("Delivery Charge", details.deliveryCharge)
        if (details.discount > 0) {
            PriceDetailRow("Discount", details.discount, isDiscount = true)
        }
        PriceDetailRow("Platform Fee", details.platformFee)
        Divider(modifier = Modifier.padding(vertical = 12.dp))
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
fun PaymentMethodSection(method: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Payment Method", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.Payments,
                    contentDescription = "Payment Icon",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(method, style = MaterialTheme.typography.bodyLarge)
                    if (method == "Cash on Delivery") {
                        Text(
                            "(Pay when your order arrives)",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.7f),
                        )
                    }
                }
                // Maybe add a "Change" button here in the future
            }
        }
    }
}

@Composable
fun CheckoutActionBar(
    totalAmount: Double,
    isLoading: Boolean,
    enabled: Boolean,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        // Use Surface for elevation and background
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp, // Lift it visually
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Total Amount", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "₹${totalAmount.formatToTwoDecimal()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            Button(
                onClick = onPlaceOrder,
                enabled = enabled && !isLoading, // Disable if address not ready or already placing order
                modifier = Modifier.height(48.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary, // Color for indicator on button
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Place Order")
                }
            }
        }
    }
}
