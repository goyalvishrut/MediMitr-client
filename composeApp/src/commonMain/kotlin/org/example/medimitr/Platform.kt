package org.example.medimitr

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
