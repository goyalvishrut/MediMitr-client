package org.example.medimitr.ui

import android.content.Context
import coil3.ImageLoader
import coil3.request.crossfade

actual object ImageLoaderFactory {
    actual fun create(context: Any): ImageLoader =
        ImageLoader
            .Builder(context as Context)
            .crossfade(true) // Example configuration
            .build()
}
