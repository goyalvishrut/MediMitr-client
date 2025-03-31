package org.example.medimitr.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AndroidTokenStorage(
    context: Context,
) : TokenStorage {
    private val sharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString(JWT_TOKEN, token).apply()
    }

    override fun getToken(): String? = sharedPreferences.getString(JWT_TOKEN, null)

    override fun clearToken() {
        sharedPreferences.edit().remove(JWT_TOKEN).apply()
    }

    companion object {
        private const val PREF_NAME = "secure_prefs"
        private const val JWT_TOKEN = "jwt_token"
    }
}
