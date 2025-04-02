package org.example.medimitr.domain.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.promotion.Category
import org.example.medimitr.domain.promotion.Promotion

class LocationRepositoryImpl : LocationRepository {
    private var selectedCity: String = "Raigarh"

    override fun getCurrentCity(): StateFlow<String> = MutableStateFlow(selectedCity).asStateFlow()

    override fun selectCity(city: String) {
        selectedCity = city
    }

    override fun getAvailableCity(): StateFlow<List<String>> = MutableStateFlow(listOf("Raigarh", "Raipur", "Bilaspur")).asStateFlow()

    override fun getPromotions(): List<Promotion> =
        listOf(
            Promotion(
                id = "1",
                title = "Buy 1 Get 1 Free",
                imageUrl = "https://example.com/promotion1.jpg",
                deeplink = null,
            ),
            Promotion(
                id = "2",
                title = "20% Off on All Medicines",
                imageUrl = "https://example.com/promotion2.jpg",
                deeplink = null,
            ),
            Promotion(
                id = "3",
                title = "Free Shipping on Orders Over $50",
                imageUrl = "https://example.com/promotion3.jpg",
                deeplink = null,
            ),
        )

    override fun getCategories(): List<Category> =
        listOf(
            Category(
                id = "1",
                name = "Pain Relief",
                iconUrl = "https://example.com/category1.jpg",
            ),
            Category(
                id = "2",
                name = "Cold & Flu",
                iconUrl = "https://example.com/category2.jpg",
            ),
            Category(
                id = "3",
                name = "Allergy Relief",
                iconUrl = "https://example.com/category3.jpg",
            ),
            Category(
                id = "4",
                name = "Digestive Health",
                iconUrl = "https://example.com/category4.jpg",
            ),
            Category(
                id = "5",
                name = "Vitamins & Supplements",
                iconUrl = "https://example.com/category5.jpg",
            ),
        )

    override fun getFeaturedMedicines(): List<Medicine> =
        listOf(
            Medicine(
                id = "1",
                name = "Paracetamol",
                description = "Pain reliever and fever reducer",
                price = 5.99,
                imageUrl = "https://example.com/medicine1.jpg",
                requiresPrescription = false,
            ),
            Medicine(
                id = "2",
                name = "Ibuprofen",
                description = "Nonsteroidal anti-inflammatory drug (NSAID)",
                price = 7.99,
                imageUrl = "https://example.com/medicine2.jpg",
                requiresPrescription = false,
            ),
            Medicine(
                id = "3",
                name = "Cetirizine",
                description = "Antihistamine for allergy relief",
                price = 9.99,
                imageUrl = "https://example.com/medicine3.jpg",
                requiresPrescription = false,
            ),
            Medicine(
                id = "4",
                name = "Omeprazole",
                description = "Proton pump inhibitor for acid reflux",
                price = 12.99,
                imageUrl = "https://example.com/medicine4.jpg",
                requiresPrescription = false,
            ),
            Medicine(
                id = "5",
                name = "Vitamin C",
                description = "Essential vitamin for immune support",
                price = 4.99,
                imageUrl = "https://example.com/medicine5.jpg",
                requiresPrescription = false,
            ),
            Medicine(
                id = "6",
                name = "Loratadine",
                description = "Antihistamine for allergy relief",
                price = 8.99,
                imageUrl = "https://example.com/medicine6.jpg",
                requiresPrescription = false,
            ),
            Medicine(
                id = "7",
                name = "Aspirin",
                description = "Pain reliever and anti-inflammatory",
                price = 6.99,
                imageUrl = "https://example.com/medicine7.jpg",
                requiresPrescription = false,
            ),
        )
}
