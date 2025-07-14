package com.jdev.petly.data.repository

import com.jdev.petly.utils.SharedPreferencesManager
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    fun getSelectedUnit(): String {
        return sharedPreferencesManager.getString("selected_unit", "kg")
    }

    fun setSelectedUnit(unit: String) {
        sharedPreferencesManager.saveString("selected_unit", unit)
    }

    fun setFilterVeterinaryVisits(filter : String) {
        sharedPreferencesManager.saveString("filer_veterinary_visits", filter)
    }

    fun getFilterVeterinaryVisits(): String {
        return sharedPreferencesManager.getString("filer_veterinary_visits", "all")
    }

    fun setFilterEvents(filter : String) {
        sharedPreferencesManager.saveString("filer_events", filter)
    }

    fun getFilterEvents(): String {
        return sharedPreferencesManager.getString("filer_events", "all")
    }

    fun setLanguage(language : String) {
        sharedPreferencesManager.saveString("language", language)
    }

    fun getLanguage(): String {
        return sharedPreferencesManager.getString("language", "es")
    }

    fun getDarkModeState(): Boolean {
        return sharedPreferencesManager.getBoolean("is_dark_mode", false)
    }

    fun setDarkModeState(isDark: Boolean) {
        sharedPreferencesManager.saveBoolean("is_dark_mode", isDark)
    }

    private val LAST_OPTIONAL_VERSION_KEY = "last_optional_version_shown"

    fun getLastOptionalVersionShown(): String? {
        return sharedPreferencesManager.getString(LAST_OPTIONAL_VERSION_KEY, "")
            .takeIf { it.isNotBlank() }
    }

    fun setLastOptionalVersionShown(version: String) {
        sharedPreferencesManager.saveString(LAST_OPTIONAL_VERSION_KEY, version)
    }
}
