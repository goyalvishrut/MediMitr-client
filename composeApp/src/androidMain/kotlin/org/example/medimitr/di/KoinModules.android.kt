package org.example.medimitr.di

import org.example.medimitr.data.local.AndroidTokenStorage
import org.example.medimitr.data.local.TokenStorage
import org.example.medimitr.ui.common.ImageLoaderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<TokenStorage> { AndroidTokenStorage(androidContext()) }
        single { (context: Any) -> ImageLoaderFactory.create(context) }
    }
