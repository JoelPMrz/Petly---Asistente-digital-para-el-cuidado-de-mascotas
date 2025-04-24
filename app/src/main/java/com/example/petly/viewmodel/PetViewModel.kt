package com.example.petly.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.Pet
import com.example.petly.data.repository.PetRepository
import com.example.petly.utils.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _petsState = MutableStateFlow<List<Pet>>(emptyList())
    val petsState: StateFlow<List<Pet>> get() = _petsState

    private val _petState = MutableStateFlow<Pet?>(null)
    val petState: StateFlow<Pet?> get() = _petState

    fun getPets() {
        viewModelScope.launch {
            try {
                petRepository.getPetsFlow().collect { pets ->
                    _petsState.value = pets
                }
            } catch (e: Exception) {
            }
        }
    }

    fun getPetById(petId: String) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                _petState.value = pet // Actualizar el estado con la mascota obtenida
            } catch (e: Exception) {
                _petState.value = null // Si ocurre un error, puedes asignar null
            }
        }
    }

    fun addPetWithImage(pet: Pet, imageUri: Uri, fileName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                petRepository.addPetWithImage(pet, imageUri, fileName)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun addPetWithoutImage(pet : Pet, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            try {
                petRepository.addPetWithoutImage(pet)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            try {
                petRepository.updatePet(pet)
                getPets()
            } catch (e: Exception) {
            }
        }
    }

    fun deletePet(petId: String) {
        viewModelScope.launch {
            try {
                petRepository.deletePet(petId)
                getPets()
            } catch (e: Exception) {

            }
        }
    }
}
