package org.example.medimitr.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.medimitr.common.formatToTwoDecimal
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.promotion.Category
import org.example.medimitr.domain.promotion.Promotion
import org.example.medimitr.ui.medicine.MedicineDetailScreen
import org.example.medimitr.ui.order.cart.CartScreen
import org.example.medimitr.ui.search.SearchScreen
import org.koin.mp.KoinPlatform.getKoin

// ui/screen/HomeScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
class HomeScreen : Screen { // Use object if no params needed

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { getKoin().get<HomeScreenModel>() }
        val state by screenModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val citySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        // Show errors
        LaunchedEffect(state.error) {
            if (state.error != null) {
                snackbarHostState.showSnackbar(state.error!!, duration = SnackbarDuration.Short)
                screenModel.clearError()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                HomeTopAppBar(
                    selectedCity = state.selectedCity,
                    cartItemCount = state.cartItems.size,
                    onCityClick = { screenModel.toggleCitySelection(true) },
                    onCartClick = { navigator.push(CartScreen()) },
                )
            },
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (state.isLoading && state.promotions.isEmpty()) { // Show loading only initially
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    HomeContent(
                        state = state,
                        onSearchClick = { navigator.push(SearchScreen()) },
                        onCategoryClick = { /* TODO: Handle category selection (filter/navigate) */ },
                        onMedicineClick = { medicineId ->
                            navigator.push(
                                MedicineDetailScreen(
                                    medicineId,
                                ),
                            )
                        },
                        onAddToCart = screenModel::onAddToCart,
                        onUpdateQuantity = screenModel::onUpdateQuantity,
                    )
                }
            }
        }

        // --- City Selection Bottom Sheet ---
        if (state.showCitySelection) {
            ModalBottomSheet(
                onDismissRequest = { screenModel.toggleCitySelection(false) },
                sheetState = citySheetState,
            ) {
                CitySelectionBottomSheetContent(
                    cities = state.availableCities,
                    selectedCity = state.selectedCity,
                    onCitySelected = screenModel::selectCity,
                )
            }
        }

        // --- Remove Item Confirmation Dialog ---
        if (state.showRemoveConfirmation && state.itemPendingRemoval != null) {
            AlertDialog(
                onDismissRequest = screenModel::cancelRemoveItem,
                title = { Text("Remove Item?") },
                text = { Text("Do you want to remove ${state.itemPendingRemoval?.name} from your cart?") },
                confirmButton = {
                    Button(onClick = screenModel::confirmRemoveItem) { Text("Remove") }
                },
                dismissButton = {
                    TextButton(onClick = screenModel::cancelRemoveItem) { Text("Cancel") }
                },
            )
        }
    }
}

// --- Top App Bar Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    selectedCity: String,
    cartItemCount: Int,
    onCityClick: () -> Unit,
    onCartClick: () -> Unit,
) {
    TopAppBar(
        title = {
            TextButton(onClick = onCityClick) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        "Deliver to",
                        style = MaterialTheme.typography.labelSmall,
                        lineHeight = 12.sp,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selectedCity,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Change city")
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onCartClick) {
                BadgedBox(badge = {
                    if (cartItemCount > 0) Badge { Text(cartItemCount.toString()) }
                }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            }
        },
        // Optional: Add colors, elevation
        // colors = TopAppBarDefaults.topAppBarColors(...)
    )
}

// --- Main Content Composable ---
@Composable
private fun HomeContent(
    state: HomeUiState,
    onSearchClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onMedicineClick: (medicineId: String) -> Unit,
    onAddToCart: (Medicine) -> Unit,
    onUpdateQuantity: (Medicine, Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp), // Padding for top/bottom of list
        verticalArrangement = Arrangement.spacedBy(20.dp), // Space between sections
    ) {
        // 1. Search Bar Placeholder
        item {
            SearchBarPlaceholder(
                onClick = onSearchClick,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // 2. Promotions Section
        if (state.promotions.isNotEmpty()) {
            item {
                SectionTitle("Offers For You", modifier = Modifier.padding(horizontal = 16.dp))
                PromotionsRow(state.promotions)
            }
        }

        // 3. Categories Section
        if (state.categories.isNotEmpty()) {
            item {
                SectionTitle("Shop by Category", modifier = Modifier.padding(horizontal = 16.dp))
                CategoriesRow(state.categories, onCategoryClick)
            }
        }

        // 4. Featured Items Section
        if (state.featuredItems.isNotEmpty()) {
            item {
                SectionTitle("Featured Products", modifier = Modifier.padding(horizontal = 16.dp))
                FeaturedItemsRow(
                    medicines = state.featuredItems,
                    cartQuantities = state.cartItems.associate { it.medicine.id to it.quantity }, // Map for quick lookup
                    onMedicineClick = onMedicineClick,
                    onAddToCart = onAddToCart,
                    onUpdateQuantity = onUpdateQuantity,
                )
            }
        }

        // Add more sections as needed (e.g., Recently Viewed, Based on Past Orders)
    }
}

// --- UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarPlaceholder(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Rounded corners
        color = MaterialTheme.colorScheme.surfaceVariant, // Different background
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(8.dp))
            Text("Search for medicines...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 8.dp),
    )
}

@Composable
fun PromotionsRow(promotions: List<Promotion>) {
    val uriHandler = LocalUriHandler.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(promotions, key = { it.id }) { promo ->
            PromotionCard(promo = promo, onClick = {
                promo.deeplink?.let { uriHandler.openUri(it) }
            })
        }
    }
}

@Composable
fun PromotionCard(
    promo: Promotion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.width(280.dp).height(140.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
    ) {
//        AsyncImage(
//            model = promo.imageUrl,
//            contentDescription = promo.title,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop, // Or Fit
//        )
    }
}

@Composable
fun CategoriesRow(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryChip(category = category, onClick = { onCategoryClick(category) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        // Using FilterChip for a selectable look, adjust if needed
        selected = false, // Selection state managed externally if filtering
        onClick = onClick,
        modifier = modifier,
        label = { Text(category.name) },
        leadingIcon = {
            if (category.iconUrl != null) {
//                AsyncImage(
//                    model = category.iconUrl,
//                    contentDescription = null,
//                    modifier = Modifier.size(FilterChipDefaults.IconSize),
//                )
            } else {
                // Fallback icon
                Icon(
                    Icons.Default.Category,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        },
        shape = CircleShape,
    )
}

@Composable
fun FeaturedItemsRow(
    medicines: List<Medicine>,
    cartQuantities: Map<String, Int>, // Pre-calculated quantities
    onMedicineClick: (medicineId: String) -> Unit,
    onAddToCart: (Medicine) -> Unit,
    onUpdateQuantity: (Medicine, Int) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(medicines, key = { it.id }) { medicine ->
            MedicineItemCard(
                medicine = medicine,
                quantityInCart = cartQuantities.get(medicine.id) ?: 0,
                onMedicineClick = onMedicineClick,
                onAddToCart = { onAddToCart(medicine) },
                onUpdateQuantity = { newQuantity -> onUpdateQuantity(medicine, newQuantity) },
            )
        }
    }
}

@Composable
fun MedicineItemCard(
    medicine: Medicine,
    quantityInCart: Int,
    onMedicineClick: (medicineId: String) -> Unit,
    onAddToCart: () -> Unit,
    onUpdateQuantity: (newQuantity: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onMedicineClick(medicine.id) }, // Make card clickable
        modifier = modifier.width(160.dp), // Fixed width for items in a row
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
//            AsyncImage(
//                model = medicine.imageUrl ?: "",
//                contentDescription = medicine.name,
//                modifier =
//                    Modifier
//                        .height(100.dp)
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(8.dp))
//                        .background(MaterialTheme.colorScheme.surfaceVariant),
//                // Placeholder bg
//                contentScale = ContentScale.Crop,
//            )
            Spacer(Modifier.height(8.dp))
            Text(
                medicine.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "₹${medicine.price.formatToTwoDecimal()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))

            // Add to Cart / Quantity Control
            if (quantityInCart == 0) {
                OutlinedButton(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    Text("Add", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                QuantitySelectorSmall(
                    // Use a smaller variant for cards
                    quantity = quantityInCart,
                    onQuantityChange = onUpdateQuantity,
                    modifier = Modifier.align(Alignment.CenterHorizontally).height(36.dp),
                )
            }
        }
    }
}

// A potentially smaller version of QuantitySelector for cards
@Composable
fun QuantitySelectorSmall(
    quantity: Int,
    onQuantityChange: (newQuantity: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp), // Reduced spacing
    ) {
        IconButton(
            onClick = { onQuantityChange(quantity - 1) },
            modifier = Modifier.size(30.dp), // Slightly smaller
        ) {
            Icon(
                imageVector = if (quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                contentDescription = if (quantity == 1) "Remove item" else "Decrease quantity",
                modifier = Modifier.size(18.dp), // Smaller icon
                tint = if (quantity == 1) MaterialTheme.colorScheme.error else LocalContentColor.current,
            )
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyMedium, // Smaller text
            fontWeight = FontWeight.Bold,
            modifier = Modifier.widthIn(min = 20.dp),
            textAlign = TextAlign.Center,
        )
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(30.dp), // Slightly smaller
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase quantity",
                modifier = Modifier.size(18.dp), // Smaller icon
            )
        }
    }
}

@Composable
fun CitySelectionBottomSheetContent(
    cities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Select Your City",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        LazyColumn {
            items(cities) { city ->
                ListItem(
                    headlineContent = { Text(city) },
                    modifier = Modifier.clickable { onCitySelected(city) },
                    trailingContent = {
                        if (city == selectedCity) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                )
            }
        }
    }
}
