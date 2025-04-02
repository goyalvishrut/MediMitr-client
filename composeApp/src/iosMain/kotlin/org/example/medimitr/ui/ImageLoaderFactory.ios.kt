package org.example.medimitr.ui

import coil3.ImageLoader
import platform.UIKit.UIImage

actual object ImageLoaderFactory {
    actual fun create(context: Any): ImageLoader = ImageLoader.Builder(context as UIImage).build()
}
