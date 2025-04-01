package org.example.medimitr.data.local

import org.example.medimitr.domain.auth.User

interface LocalStorage {
    fun saveUser(user: User)

    fun getUser(): User?

    fun clearUser()
}
