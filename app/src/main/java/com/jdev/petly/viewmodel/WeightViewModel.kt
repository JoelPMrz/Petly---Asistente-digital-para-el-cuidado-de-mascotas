package com.jdev.petly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdev.petly.data.models.Weight
import com.jdev.petly.data.repository.WeightRepository
import com.jdev.petly.utils.convertWeight
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

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


    fun addWeight(petId: String, weight: Weight, notPermission: () -> Unit) {
        viewModelScope.launch {
            try {
                weightRepository.addWeightToPet(petId, weight)
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            }catch (e: Exception) {
                _errorState.value = "Error al agregar peso: ${e.message}"
            }
        }
    }


    fun updateWeight(weight: Weight, notPermission: () -> Unit, weightNotExist:()-> Unit) {
        viewModelScope.launch {
            try {
                val existingWeight = weight.id?.let { weightRepository.getWeightById(it) }
                if (existingWeight != null) {
                    weightRepository.updateWeight(weight)
                } else {
                    weightNotExist()
                }
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            } catch (e: Exception) {
                _errorState.value = "Error al actualizar peso: ${e.message}"
            }
        }
    }


    fun deleteWeight(petId: String, weightId: String, notPermission: () -> Unit) {
        viewModelScope.launch {
            try {
                weightRepository.deleteWeightFromPet(petId, weightId)
                getWeights(petId)
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
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

    fun comparePreviousWeight(weight: Weight, weights: List<Weight>, selectedUnit: String): Double? {
        val position = weights.indexOf(weight)
        if (position <= 0) return null

        val previousWeight = weights[position - 1]

        val previousConverted = convertWeight(previousWeight.value, previousWeight.unit, selectedUnit)
        val currentConverted = convertWeight(weight.value, weight.unit, selectedUnit)

        val difference = currentConverted - previousConverted

        return (round(difference * 100) / 100)
    }
}