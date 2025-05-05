package com.example.petly.viewmodel

import androidx.lifecycle.ViewModel
import com.example.petly.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {


    private val _selectedUnit = MutableStateFlow(preferencesRepository.getSelectedUnit())
    val selectedUnit: StateFlow<String> = _selectedUnit

    private val _isDarkMode = MutableStateFlow(preferencesRepository.getDarkModeState())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun setSelectedUnit(unit: String) {
        preferencesRepository.setSelectedUnit(unit)
        _selectedUnit.value = unit
    }

    fun setDarkMode(isDark: Boolean) {
        preferencesRepository.setDarkModeState(isDark)
        _isDarkMode.value = isDark
    }



    fun reloadUnitPreference() {
        _selectedUnit.value = preferencesRepository.getSelectedUnit()
    }
}
