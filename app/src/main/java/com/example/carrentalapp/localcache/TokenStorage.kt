package com.example.carrentalapp.localcache

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object TokenStorage {
    private const val PREF_FILE = "secure_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"

    // In-memory cache for JwtInterceptor (no context needed there)
    var token: String? = null
        private set

    private fun getPrefs(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREF_FILE,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun save(context: Context, jwtToken: String, userId: String, email: String, role: String) {
        token = jwtToken
        getPrefs(context).edit()
            .putString(KEY_TOKEN, jwtToken)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun load(context: Context) {
        token = getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun getUserId(context: Context): String? = getPrefs(context).getString(KEY_USER_ID, null)

    fun getEmail(context: Context): String? = getPrefs(context).getString(KEY_EMAIL, null)

    fun getRole(context: Context): String? = getPrefs(context).getString(KEY_ROLE, null)

    fun clear(context: Context) {
        token = null
        getPrefs(context).edit().clear().apply()
    }

    fun hasToken(context: Context): Boolean = getPrefs(context).getString(KEY_TOKEN, null) != null
}
