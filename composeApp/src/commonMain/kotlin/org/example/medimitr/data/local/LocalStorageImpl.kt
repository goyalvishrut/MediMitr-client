package org.example.medimitr.data.local

import com.russhwolf.settings.Settings
import org.example.medimitr.domain.auth.User

class LocalStorageImpl(
    private val settings: Settings,
) : LocalStorage {
    override fun saveUser(user: User) {
        settings.putInt(USER_ID, user.id)
        settings.putString(USER_EMAIL, user.email)
        settings.putString(USER_ADDRESS, user.address)
        settings.putString(USER_PHONE, user.phone)
        settings.putString(NAME, user.name)
    }

    override fun getUser(): User? {
        val id = settings.getInt(USER_ID, -1)
        if (id == -1) return null
        val email = settings.getString(USER_EMAIL, "")
        val address = settings.getString(USER_ADDRESS, "")
        val phone = settings.getString(USER_PHONE, "")
        val name = settings.getString(NAME, "")
        return User(id, email, address, phone, name)
    }

    override fun clearUser() {
        settings.remove(USER_ID)
        settings.remove(USER_EMAIL)
        settings.remove(USER_ADDRESS)
        settings.remove(USER_PHONE)
        settings.remove(NAME)
    }

    companion object {
        private const val USER_ID = "user_id"
        private const val USER_EMAIL = "user_email"
        private const val USER_ADDRESS = "user_address"
        private const val USER_PHONE = "user_phone"
        private const val NAME = "name"
    }
}
