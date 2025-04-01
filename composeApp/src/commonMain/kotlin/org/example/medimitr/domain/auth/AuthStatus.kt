package org.example.medimitr.domain.auth

enum class AuthStatus {
    LOGGED_IN,
    LOGGED_OUT,
    UNKNOWN, // UNKNOWN for initial loading
}
