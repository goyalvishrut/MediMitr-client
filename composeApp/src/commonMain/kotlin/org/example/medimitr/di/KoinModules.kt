package org.example.medimitr.di

// Import other ViewModels, Repositories, DataSources as you create them

import org.example.medimitr.data.medicine.MedicineRemoteDataSource
import org.example.medimitr.data.medicine.MedicineRemoteDataSourceImpl
import org.example.medimitr.domain.medicine.MedicineRepository
import org.example.medimitr.domain.medicine.MedicineRepositoryImpl
import org.example.medimitr.network.createHttpClient
import org.example.medimitr.presentation.checkout.CheckoutViewModel
import org.example.medimitr.presentation.search.SearchResultsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// Network Module
val networkModule =
    module {
        single { createHttpClient() } // Provides the Ktor HttpClient
        // You might provide the Base URL here if needed elsewhere
        // single { "YOUR_BASE_API_URL" }
    }

// Data Module
val dataModule =
    module {
        // DataSources
        single<MedicineRemoteDataSource> { MedicineRemoteDataSourceImpl(get()) }
        // Add bindings for AuthRemoteDataSource, OrderRemoteDataSource etc.

        // Repositories
        single<MedicineRepository> { MedicineRepositoryImpl(get()) }
        // Add bindings for AuthRepository, OrderRepository, CartRepository etc.
    }

// ViewModel Module - Use singleOf for simpler ViewModel definition
val viewModelModule =
    module {
        singleOf(::SearchResultsViewModel)
        singleOf(::CheckoutViewModel)
        // Add other ViewModels: AuthViewModel, CartViewModel, CheckoutViewModel, OrderHistoryViewModel
    }

// List of all modules
val sharedModules = listOf(networkModule, dataModule, viewModelModule)
