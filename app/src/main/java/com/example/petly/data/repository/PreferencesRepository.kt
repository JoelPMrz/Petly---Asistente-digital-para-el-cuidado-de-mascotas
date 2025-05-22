package com.example.petly.data.repository

import com.example.petly.utils.SharedPreferencesManager
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
        sharedPreferencesManager.saveString("filer_veterinay_visits", filter)
    }

    fun getFilterVeterinaryVisits(): String {
        return sharedPreferencesManager.getString("filer_veterinay_visits", "all")
    }

    fun getDarkModeState(): Boolean {
        return sharedPreferencesManager.getBoolean("is_dark_mode", false)
    }

    fun setDarkModeState(isDark: Boolean) {
        sharedPreferencesManager.saveBoolean("is_dark_mode", isDark)
    }

}
