package org.example.medimitr.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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

    @OptIn(ExperimentalEncodingApi::class)
    override fun isTokenValid(): Boolean {
        val token = getToken() ?: return false
        try {
            // Split JWT into header, payload, and signature
            val parts = token.split(".")
            if (parts.size != 3) return false

            // Decode the payload (second part)
            val payload = String(Base64.UrlSafe.decode(parts[1]))
            val payloadJson = Json.parseToJsonElement(payload) as JsonObject
            val expiration = payloadJson["exp"]?.jsonPrimitive?.longOrNull ?: return false
            val currentTimeSeconds = System.currentTimeMillis() / 1000

            return currentTimeSeconds < expiration
        } catch (e: Exception) {
            return false // Invalid token format or decoding error
        }
    }

    companion object {
        private const val PREF_NAME = "secure_prefs"
        private const val JWT_TOKEN = "jwt_token"
    }
}
