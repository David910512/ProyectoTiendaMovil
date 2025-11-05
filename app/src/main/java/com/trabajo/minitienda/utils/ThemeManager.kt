package com.trabajo.minitienda.utils


import android.content.Context

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager(context: Context) {
    private val prefs = context.getSharedPreferences("mini_tienda_theme", Context.MODE_PRIVATE)
    private val THEME_KEY = "is_dark_theme"

    private val _isDarkTheme = MutableStateFlow(getSavedTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private fun getSavedTheme(): Boolean {
        return prefs.getBoolean(THEME_KEY, false) // false = tema claro por defecto
    }

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        prefs.edit().putString(THEME_KEY, newTheme.toString()).apply()
    }

    fun setTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        prefs.edit().putBoolean(THEME_KEY, isDark).apply()
    }
}