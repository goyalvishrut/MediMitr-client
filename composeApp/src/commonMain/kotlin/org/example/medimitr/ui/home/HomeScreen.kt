package org.example.medimitr.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.medicine.Medicine
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/HomeScreen.kt
class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { getKoin().get<HomeScreenModel>() }
        val cartRepository = getKoin().get<CartRepository>()

        // Load medicines when the screen is first displayed
        LaunchedEffect(Unit) {
            screenModel.start()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MediMitr") },
                    actions = {
                        IconButton(onClick = { /* TODO: Navigate to cart */ }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                        IconButton(onClick = { /* TODO: Navigate to profile */ }) {
                            Icon(Icons.Default.LocationCity, contentDescription = "Place")
                        }
                    },
                )
            },
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
            ) {
                // Styled Search Bar
                TextField(
                    value = screenModel.searchQuery,
                    onValueChange = { screenModel.searchQuery = it },
                    label = { Text("Search Medicines") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { screenModel.onSearch() },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Search")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Loading, Empty, or Content States
                when {
                    screenModel.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    screenModel.medicines.isEmpty() -> {
                        Text(
                            "No medicines found",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }

                    else -> {
                        LazyColumn {
                            // Promotional Banners Section
                            item {
                                Text(
                                    "Promotions",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(8.dp),
                                )
                                LazyRow {
                                    // Mock promotional items (replace with real data later)
                                    items(listOf("Promo 1", "Promo 2", "Promo 3")) { promo ->
                                        Card(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                promo,
                                                modifier = Modifier.padding(16.dp),
                                            )
                                        }
                                    }
                                }
                            }
                            // Medicines List Section
                            item {
                                Text(
                                    "Medicines",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                            items(screenModel.medicines) { medicine ->
                                MedicineCard(medicine, screenModel, cartRepository)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineCard(
    medicine: Medicine,
    screenModel: HomeScreenModel,
    cartRepository: CartRepository,
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp),
        ) {
            // Assuming Medicine has an imageUrl field
//            Image(
//                painter = rememberImagePainter(medicine.imageUrl ?: ""),
//                contentDescription = null,
//                modifier = Modifier.size(64.dp)
//            )
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(medicine.name, style = MaterialTheme.typography.bodyLarge)
                Text("$${medicine.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = { screenModel.onAddToCart(medicine, cartRepository) }) {
                Text("Add to Cart")
            }
        }
    }
}
