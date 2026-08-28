package com.snap2card.feature.settings.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.snap2card.feature.settings.domain.model.UserSettings
import com.snap2card.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    companion object {
        private val KEY_DAILY_GOAL = intPreferencesKey("daily_goal")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    override fun getSettings(): Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            dailyGoalCards = prefs[KEY_DAILY_GOAL] ?: 20,
            darkMode = prefs[KEY_DARK_MODE] ?: false,
            notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
            language = prefs[KEY_LANGUAGE] ?: "en",
        )
    }

    override suspend fun updateSettings(settings: UserSettings) {
        dataStore.edit { prefs ->
            prefs[KEY_DAILY_GOAL] = settings.dailyGoalCards
            prefs[KEY_DARK_MODE] = settings.darkMode
            prefs[KEY_NOTIFICATIONS] = settings.notificationsEnabled
            prefs[KEY_LANGUAGE] = settings.language
        }
    }
}
