package com.example.colorassistant

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("settings")

object LanguagePreferences {

    private val LANGUAGE_KEY = stringPreferencesKey("language")

    suspend fun getLanguage(context: Context): String {
        return context.dataStore.data.first()[LANGUAGE_KEY] ?: "en"
    }

    suspend fun saveLanguage(context: Context, language: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language
        }
    }
}
