package org.example.medimitr.ui.common

import coil3.ImageLoader

expect object ImageLoaderFactory {
    fun create(context: Any): ImageLoader
}
