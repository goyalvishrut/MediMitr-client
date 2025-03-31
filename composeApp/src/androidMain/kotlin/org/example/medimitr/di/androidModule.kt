package org.example.medimitr.di

import org.example.medimitr.data.local.AndroidTokenStorage
import org.example.medimitr.data.local.TokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule =
    module {
        single<TokenStorage> { AndroidTokenStorage(androidContext()) }
    }
