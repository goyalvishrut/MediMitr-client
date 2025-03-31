package org.example.medimitr.ui.screens.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.medimitr.ui.screenmodel.CheckoutScreenModel
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/CheckoutScreen.kt
class CheckoutScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { getKoin().get<CheckoutScreenModel>() }
        val navigator = LocalNavigator.currentOrThrow
        val cartItems by screenModel.cartItems.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Order Summary", style = MaterialTheme.typography.headlineMedium)
            LazyColumn {
                items(cartItems) { item ->
                    Text("${item.medicine.name} x${item.quantity} - $${item.medicine.price * item.quantity}")
                }
            }
            Spacer(Modifier.height(16.dp))
            val total = cartItems.sumOf { it.medicine.price * it.quantity }
            Text("Total: $$total", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            if (screenModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            screenModel.errorMessage?.let {
                Text(it, color = Color.Red)
            }
            Button(
                onClick = { screenModel.onPlaceOrder(navigator) },
                enabled = !screenModel.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Place Order")
            }
        }
    }
}
