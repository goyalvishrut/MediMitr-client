package org.example.medimitr.di

import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.api.ApiServiceImpl
import org.example.medimitr.data.medicine.MedicineRemoteDataSource
import org.example.medimitr.data.medicine.MedicineRemoteDataSourceImpl
import org.example.medimitr.domain.auth.AuthRepository
import org.example.medimitr.domain.auth.AuthRepositoryImpl
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.cart.CartRepositoryImpl
import org.example.medimitr.domain.medicine.MedicineRepository
import org.example.medimitr.domain.medicine.MedicineRepositoryImpl
import org.example.medimitr.domain.order.OrderRepository
import org.example.medimitr.domain.order.OrderRepositoryImpl
import org.example.medimitr.network.createHttpClient
import org.example.medimitr.presentation.checkout.CheckoutViewModel
import org.example.medimitr.presentation.search.SearchResultsViewModel
import org.example.medimitr.ui.screenmodel.CartScreenModel
import org.example.medimitr.ui.screenmodel.CheckoutScreenModel
import org.example.medimitr.ui.screenmodel.LoginScreenModel
import org.example.medimitr.ui.screenmodel.SearchScreenModel
import org.example.medimitr.ui.screenmodel.SignupScreenModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// Network Module
val networkModule =
    module {
        single { createHttpClient() } // Provides the Ktor HttpClient
        // You might provide the Base URL here if needed elsewhere
        single<ApiService> { ApiServiceImpl(get()) }
        single<MedicineRemoteDataSource> { MedicineRemoteDataSourceImpl(get()) }
    }

// Data Module
val dataModule =
    module {
        // Repositories
        single<MedicineRepository> { MedicineRepositoryImpl(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<CartRepository> { CartRepositoryImpl() }
        single<OrderRepository> { OrderRepositoryImpl(get()) }
    }

// ViewModel Module - Use singleOf for simpler ViewModel definition
val viewModelModule =
    module {
        singleOf(::SearchResultsViewModel)
        singleOf(::CheckoutViewModel)
        // Add other ViewModels: AuthViewModel, CartViewModel, CheckoutViewModel, OrderHistoryViewModel
    }

val screenModelModule =
    module {
        factory { LoginScreenModel(get()) }
        factory { SignupScreenModel(get()) }
        factory { SearchScreenModel(get()) }
        factory { CartScreenModel(get()) }
        factory { CheckoutScreenModel(get(), get()) }
    }

// List of all modules
val sharedModules = listOf(networkModule, dataModule, viewModelModule, screenModelModule)
