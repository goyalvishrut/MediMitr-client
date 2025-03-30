package org.example.medimitr.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

internal actual fun httpClientEngine(): HttpClientEngine = Android.create()
