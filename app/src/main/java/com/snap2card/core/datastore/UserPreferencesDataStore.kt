package com.snap2card.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import com.snap2card.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Single source of truth for user preferences and session tokens.
 * All sensitive data should be stored here — never in SharedPreferences.
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")
        private val KEY_PHOTO_URL = stringPreferencesKey("photo_url")
    }

    val accessToken: Flow<String?> = dataStore.data.map { it[KEY_ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[KEY_REFRESH_TOKEN] }
    
    val currentUser: Flow<User?> = dataStore.data.map { prefs ->
        val id = prefs[KEY_USER_ID] ?: return@map null
        val email = prefs[KEY_EMAIL] ?: return@map null
        val name = prefs[KEY_DISPLAY_NAME] ?: email.substringBefore("@")
        val photo = prefs[KEY_PHOTO_URL]
        User(id, email, name, photo)
    }

    suspend fun saveSession(
        accessToken: String, 
        refreshToken: String, 
        userId: String,
        email: String,
        displayName: String,
        photoUrl: String? = null
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_ID] = userId
            prefs[KEY_EMAIL] = email
            prefs[KEY_DISPLAY_NAME] = displayName
            if (photoUrl != null) {
                prefs[KEY_PHOTO_URL] = photoUrl
            } else {
                prefs.remove(KEY_PHOTO_URL)
            }
        }
    }

    suspend fun updateUser(email: String, displayName: String, photoUrl: String? = null) {
        dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_DISPLAY_NAME] = displayName
            if (photoUrl != null) {
                prefs[KEY_PHOTO_URL] = photoUrl
            }
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_DISPLAY_NAME)
            prefs.remove(KEY_PHOTO_URL)
        }
    }
}
