package org.example.medimitr.di

import com.russhwolf.settings.Settings
import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.api.ApiServiceImpl
import org.example.medimitr.data.local.TokenManager
import org.example.medimitr.data.medicine.MedicineRemoteDataSource
import org.example.medimitr.data.medicine.MedicineRemoteDataSourceImpl
import org.example.medimitr.domain.auth.AuthRepository
import org.example.medimitr.domain.auth.AuthRepositoryImpl
import org.example.medimitr.domain.auth.UserRepository
import org.example.medimitr.domain.auth.UserRepositoryImpl
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.cart.CartRepositoryImpl
import org.example.medimitr.domain.location.LocationRepository
import org.example.medimitr.domain.location.LocationRepositoryImpl
import org.example.medimitr.domain.marketing.MarketingRepository
import org.example.medimitr.domain.marketing.MarketingRepositoryImpl
import org.example.medimitr.domain.medicine.MedicineRepository
import org.example.medimitr.domain.medicine.MedicineRepositoryImpl
import org.example.medimitr.domain.order.OrderRepository
import org.example.medimitr.domain.order.OrderRepositoryImpl
import org.example.medimitr.network.createHttpClient
import org.example.medimitr.presentation.checkout.CheckoutViewModel
import org.example.medimitr.presentation.search.SearchResultsViewModel
import org.example.medimitr.ui.account.screenmodel.AccountSettingViewModel
import org.example.medimitr.ui.auth.login.LoginScreenViewModel
import org.example.medimitr.ui.auth.signup.SignupScreenViewModel
import org.example.medimitr.ui.home.HomeScreenViewModel
import org.example.medimitr.ui.order.cart.CartScreenViewModel
import org.example.medimitr.ui.order.checkout.CheckoutScreenViewModel
import org.example.medimitr.ui.order.orderhistory.OrderDetailScreenViewModel
import org.example.medimitr.ui.order.orderhistory.OrderHistoryScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val localDataSourceModule =
    module {
        single<Settings> { Settings() } // Use Settings() constructor
        single { TokenManager(get()) }
    }

// Network Module
val networkModule =
    module {
        single { createHttpClient() } // Provides the Ktor HttpClient
        // You might provide the Base URL here if needed elsewhere
        single<ApiService> { ApiServiceImpl(get(), get()) }
        single<MedicineRemoteDataSource> { MedicineRemoteDataSourceImpl(get()) }
    }

// Data Module
val dataModule =
    module {
        // Repositories
        single<MedicineRepository> { MedicineRepositoryImpl(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
        single<CartRepository> { CartRepositoryImpl() }
        single<OrderRepository> { OrderRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get(), get()) }
        single<LocationRepository> { LocationRepositoryImpl() }
        single<MarketingRepository> { MarketingRepositoryImpl(get()) }
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
        viewModelOf(::LoginScreenViewModel)
        viewModelOf(::SignupScreenViewModel)
        viewModelOf(::HomeScreenViewModel)
        viewModelOf(::CartScreenViewModel)
        viewModelOf(::CheckoutScreenViewModel)
        viewModelOf(::OrderDetailScreenViewModel)
        viewModelOf(::OrderHistoryScreenViewModel)
        viewModelOf(::AccountSettingViewModel)
    }

// List of all modules
val sharedModules =
    listOf(localDataSourceModule, networkModule, dataModule, viewModelModule, screenModelModule)
