package com.example.petly.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.Pet
import com.example.petly.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
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
                _petState.value = pet
            } catch (e: Exception) {
                _petState.value = null
            }
        }
    }

    fun doesPetExist(
        petId: String,
        exists: () -> Unit,
        notExists: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                if (pet != null) {
                    exists()
                } else {
                    notExists()
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    //Ya no es necesario
    //Las mascotas se crean con imagen preterminada y posteriormente se actualiza
    //la imagen en hilo secundario (proceso más rapido)
    fun addPetWithImage(
        pet: Pet,
        imageUri: Uri,
        fileName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.addPetWithImage(pet, imageUri, fileName)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    fun addPet(
        pet: Pet,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.addPet(pet)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
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

    private var uploadJob: Job? = null

    fun updatePetProfilePhoto(
        petId: String,
        newPhotoUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        uploadJob?.cancel()
        uploadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val newPhotoUrl = petRepository.updatePetProfilePhoto(petId, newPhotoUri)
                withContext(Dispatchers.Main) {
                    _petState.value?.let {
                        _petState.value = it.copy(photo = newPhotoUrl.toString())
                    }
                    getPetById(petId)
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    fun updateBirthdate(
        petId: String,
        birthDate: LocalDate?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateBirthdate(petId, birthDate)
                getPetById(petId)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateAdoptionDate(
        petId: String,
        adoptionDate: LocalDate?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateAdoptionDate(petId, adoptionDate)
                getPetById(petId)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateSterilizedInfo(
        petId: String,
        sterilized: Boolean,
        sterilizedDate: LocalDate?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateSterilizationInfo(petId, sterilized, sterilizedDate)
                getPetById(petId)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateMicrochipInfo(
        petId: String,
        microchipId: String,
        microchipDate: LocalDate?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateMicrochipInfo(petId, microchipId, microchipDate)
                getPetById(petId)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    fun deletePet(
        petId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.deletePet(petId)
                getPets()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
