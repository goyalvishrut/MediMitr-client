package org.example.medimitr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import org.example.medimitr.ui.screenmodel.CartScreenModel
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/CartScreen.kt
class CartScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { getKoin().get<CartScreenModel>() }
        val navigator = LocalNavigator.currentOrThrow

        val cartItems by screenModel.cartItems.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn {
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("${item.medicine.name} x${item.quantity} - $${item.medicine.price * item.quantity}")
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { screenModel.onRemoveFromCart(item.medicine.id) }) {
                            Text("Remove")
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            val total = cartItems.sumOf { it.medicine.price * it.quantity }
            Text("Total: $$total", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { screenModel.onProceedToCheckout(navigator) },
                enabled = cartItems.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}
