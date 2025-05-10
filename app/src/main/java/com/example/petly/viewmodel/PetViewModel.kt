package com.example.petly.ui.viewmodel

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.Pet
import com.example.petly.data.repository.PetRepository
import com.google.firebase.firestore.FirebaseFirestoreException
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

    private val _observedPetsState = MutableStateFlow<List<Pet>>(emptyList())
    val observedPetsState: StateFlow<List<Pet>> get() = _observedPetsState

    fun getPets() {
        viewModelScope.launch {
            try {
                petRepository.getPetsFlow().collect { pets ->
                    if (_petsState.value != pets) {
                        _petsState.value = pets
                    }
                }
            } catch (e: Exception) {
                //
            }
        }
    }

    fun getObservedPets() {
        viewModelScope.launch {
            try {
                petRepository.getObservedPetsFlow().collect { pets ->
                    if (_observedPetsState.value != pets) {
                        _observedPetsState.value = pets
                    }
                }
            } catch (e: Exception) {
                //
            }
        }
    }

    fun getObservedPet(petId: String) {
        viewModelScope.launch {
            petRepository.getPetFlowById(petId).collect { pet ->
                _petState.value = pet
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
                //
            }
        }
    }

    private var uploadJob: Job? = null

    fun updatePetProfilePhoto(
        petId: String,
        newPhotoUri: Uri,
        notPermission: () -> Unit,
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
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    fun updateBasicData(
        petId: String,
        name: String,
        type: String,
        breed: String?,
        gender: String,
        notPermission: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateBasicData(petId, name, type, breed, gender)
                getPetById(petId)
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateBirthdate(
        petId: String,
        birthDate: LocalDate?,
        notPermission: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateBirthdate(petId, birthDate)
                getPetById(petId)
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateAdoptionDate(
        petId: String,
        adoptionDate: LocalDate?,
        notPermission: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateAdoptionDate(petId, adoptionDate)
                getPetById(petId)
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateSterilizedInfo(
        petId: String,
        sterilized: Boolean,
        sterilizedDate: LocalDate?,
        notPermission: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateSterilizationInfo(petId, sterilized, sterilizedDate)
                getPetById(petId)
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateMicrochipInfo(
        petId: String,
        microchipId: String,
        microchipDate: LocalDate?,
        notPermission: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.updateMicrochipInfo(petId, microchipId, microchipDate)
                getPetById(petId)
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateCreatorOwner(
        petId: String,
        newCreatorOwnerId: String,
        notPermission: () -> Unit,
        isCurrentCreatorOwner: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                val currentCreatorOwner = pet?.creatorOwner ?: ""
                val currentObservers = pet?.observers ?: emptyList()

                if (currentCreatorOwner == newCreatorOwnerId) {
                    isCurrentCreatorOwner()
                } else {
                    if (currentObservers.contains(newCreatorOwnerId)) {
                        petRepository.deletePetObserver(petId, newCreatorOwnerId)
                    }
                    petRepository.updatePetCreatorOwner(petId, newCreatorOwnerId)
                    getPetById(petId)
                    onSuccess()
                }
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun addPetOwner(
        petId: String,
        userIdToAdd: String,
        notPermission: () -> Unit,
        existsYet: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                val currentOwners = pet?.owners ?: emptyList()
                val currentObservers = pet?.observers ?: emptyList()

                if (currentOwners.contains(userIdToAdd)) {
                    existsYet()
                } else {
                    if (currentObservers.contains(userIdToAdd)) {
                        petRepository.deletePetObserver(petId, userIdToAdd)
                    }
                    petRepository.addPetOwner(petId, userIdToAdd)
                    getPetById(petId)
                    getPets()
                    onSuccess()
                }
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun addPetObserver(
        petId: String,
        userIdToAdd: String,
        notPermission: () -> Unit,
        existsYet: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                val currentObservers = pet?.observers ?: emptyList()
                val currentOwners = pet?.owners ?: emptyList()

                if (currentObservers.contains(userIdToAdd)) {
                    existsYet()
                } else {
                    if (currentOwners.contains(userIdToAdd)) {
                        petRepository.deletePetOwner(petId, userIdToAdd)
                    }
                    petRepository.addPetObserver(petId, userIdToAdd)
                    getPetById(petId)
                    getObservedPets()
                    onSuccess()
                }
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    fun deletePetObserver(
        petId: String,
        userIdToRemove: String,
        notExistsYet: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                val currentObservers = pet?.observers ?: emptyList()

                if (currentObservers.contains(userIdToRemove)) {
                    petRepository.deletePetObserver(petId, userIdToRemove)
                    getPetById(petId)
                    getObservedPets()
                    onSuccess()
                } else {
                    notExistsYet()
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun deletePetOwner(
        petId: String,
        userIdToRemove: String,
        notExistsYet: () -> String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(petId)
                val currentOwners = pet?.owners ?: emptyList()

                if (currentOwners.contains(userIdToRemove)) {
                    petRepository.deletePetOwner(petId, userIdToRemove)
                    getPetById(petId)
                    getPets()
                    onSuccess()
                } else {
                    notExistsYet()
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    fun deletePet(
        petId: String,
        notPermission: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                petRepository.deletePet(petId)
                getPets()
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
