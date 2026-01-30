package com.example.colorassistant

import android.content.Context
import androidx.core.graphics.toColorInt
import androidx.core.content.edit

object FavoritesManager {

    private const val PREFS_NAME = "color_prefs"
    private const val KEY_FAVORITES = "favorites"

    fun saveColor(context: Context, color: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colors = prefs.getStringSet(KEY_FAVORITES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        colors.add(colorToHex(color))
        prefs.edit { putStringSet(KEY_FAVORITES, colors) }
    }

    fun getFavorites(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colors = prefs.getStringSet(KEY_FAVORITES, setOf()) ?: setOf()
        return colors.map { hexToColor(it) }
    }

    private fun colorToHex(color: Int): String {
        return String.format("#%08X", color)
    }

    private fun hexToColor(hex: String): Int {
        return hex.toColorInt()
    }

    fun removeColor(context: Context, color: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colors = prefs.getStringSet(KEY_FAVORITES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        colors.remove(colorToHex(color))
        prefs.edit { putStringSet(KEY_FAVORITES, colors) }
    }

    fun isFavorite(context: Context, color: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colors = prefs.getStringSet(KEY_FAVORITES, setOf()) ?: setOf()
        return colors.contains(colorToHex(color))
    }

}
