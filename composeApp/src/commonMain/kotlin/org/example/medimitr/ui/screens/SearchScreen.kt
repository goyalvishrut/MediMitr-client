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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.ui.screenmodel.SearchScreenModel
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/SearchScreen.kt
class SearchScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { getKoin().get<SearchScreenModel>() }
        val cartRepository = getKoin().get<CartRepository>()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            TextField(
                value = screenModel.searchQuery,
                onValueChange = { screenModel.searchQuery = it },
                label = { Text("Search Medicines") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { screenModel.onSearch() }) {
                Text("Search")
            }
            Spacer(Modifier.height(8.dp))
            if (screenModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(screenModel.medicines) { medicine ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${medicine.name} - $${medicine.price}")
                            Spacer(Modifier.weight(1f))
                            Button(onClick = {
                                screenModel.onAddToCart(
                                    medicine,
                                    cartRepository,
                                )
                            }) {
                                Text("Add to Cart")
                            }
                        }
                    }
                }
            }
        }
    }
}
