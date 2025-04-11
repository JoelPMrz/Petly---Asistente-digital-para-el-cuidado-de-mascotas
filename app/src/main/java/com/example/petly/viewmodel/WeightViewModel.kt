package com.example.petly.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.Weight
import com.example.petly.data.repository.WeightRepository
import com.example.petly.navegation.Weights
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val weightRepository: WeightRepository
) : ViewModel() {

    private val _weightsState = MutableStateFlow<List<Weight>>(emptyList())
    val weightsState: StateFlow<List<Weight>> get() = _weightsState

    private val _weightState = MutableStateFlow<Weight?>(null)
    val weightState: StateFlow<Weight?> get() = _weightState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState


    fun getWeights(petId: String) {
        viewModelScope.launch {
            try {
                weightRepository.getWeightsFlow(petId).collect { weights ->
                    _weightsState.value = weights
                }
            } catch (e: Exception) {
                _errorState.value = "Error al obtener los pesos: ${e.message}"
            }
        }
    }


    fun addWeight(petId: String, weight: Weight) {
        viewModelScope.launch {
            try {
                weightRepository.addWeightToPet(petId, weight)
            } catch (e: Exception) {
                _errorState.value = "Error al agregar peso: ${e.message}"
            }
        }
    }


    fun updateWeight(weight: Weight) {
        viewModelScope.launch {
            try {
                weightRepository.updateWeight(weight)
            } catch (e: Exception) {
                _errorState.value = "Error al actualizar peso: ${e.message}"
            }
        }
    }


    fun deleteWeight(petId: String, weightId: String) {
        viewModelScope.launch {
            try {
                weightRepository.deleteWeightFromPet(petId, weightId)
                getWeights(petId)
            } catch (e: Exception) {
                _errorState.value = "Error al eliminar peso: ${e.message}"
            }
        }
    }


    fun getWeightById(weightId: String) {
        viewModelScope.launch {
            try {
                val weight = weightRepository.getWeightById(weightId)
                _weightState.value = weight
            } catch (e: Exception) {
                _errorState.value = "Error al obtener el peso: ${e.message}"
            }
        }
    }


    fun clearError() {
        _errorState.value = null
    }

    fun comparePreviousWeight(weight: Weight, weights: List<Weight>): Double? {
        Log.d("LISTA PESOS", "comparePreviousWeight: ${weights}")
        val position = weights.indexOf(weight)
        Log.d("LISTA PESOS", "Posicion: $position")
        if (position <= 0) return null
        val weightToCompare = weights[position - 1]
        Log.d("LISTA PESOS", "Peso a comparar: $weightToCompare con posicion: ${weights.indexOf(weightToCompare)}")
        return weight.value - weightToCompare.value
    }
}