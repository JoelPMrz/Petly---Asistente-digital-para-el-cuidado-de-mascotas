package com.jdev.petly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdev.petly.data.models.VeterinaryVisit
import com.jdev.petly.data.repository.VeterinaryVisitsRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VeterinaryVisitsViewModel @Inject constructor(
    private val veterinaryVisitsRepository: VeterinaryVisitsRepository
) : ViewModel() {

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _veterinaryVisits = MutableStateFlow<List<VeterinaryVisit>> (emptyList())
    val veterinaryVisits : StateFlow<List<VeterinaryVisit>>  get() = _veterinaryVisits

    fun getVeterinaryVisitsFlow(
        petId: String
    ){
        viewModelScope.launch {
            try {
                veterinaryVisitsRepository.getVeterinaryVisitsFlow(
                    petId = petId
                ).collect { veterinaryVisits ->
                    _veterinaryVisits.value = veterinaryVisits
                }
            }catch (e: Exception){
                _errorState.value = "Error al obtener las visitas veterinarias: ${e.message}"
            }
        }
    }

    private val _veterinaryVisit = MutableStateFlow<VeterinaryVisit?> (null)
    val veterinaryVisit : StateFlow<VeterinaryVisit?>  get() = _veterinaryVisit

    fun getVeterinaryVisitByIdd(){
        viewModelScope.launch {
            try {

            }catch (e: Exception){

            }
        }
    }

    fun addVeterinaryVisit(petId: String,veterinaryVisit: VeterinaryVisit, notPermission : () -> Unit, onFailure : () -> Unit){
        viewModelScope.launch {
            try {
                veterinaryVisitsRepository.addVeterinaryVisitToPet(petId ,veterinaryVisit)
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure()
                _errorState.value = "Error al agregar peso: ${e.message}"
            }
        }
    }

    fun updateVeterinaryVisit(veterinaryVisit: VeterinaryVisit, veterinaryVisitNotExist:()-> Unit, notPermission : () -> Unit, onFailure : () -> Unit){
        viewModelScope.launch {
            try {
                val existingVeterinaryVisit = veterinaryVisitsRepository.getVeterinaryVisitById(veterinaryVisit.id)
                if(existingVeterinaryVisit != null){
                    veterinaryVisitsRepository.updateVeterinaryVisit(veterinaryVisit)
                }else{
                    veterinaryVisitNotExist()
                }
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            } catch (e: Exception) {
                onFailure()
                _errorState.value = "Error al actualizar peso: ${e.message}"
            }
        }
    }

    fun deleteVeterinaryVisit(petId: String, veterinaryVisitId: String, notPermission: () -> Unit){
        viewModelScope.launch {
            try {
                veterinaryVisitsRepository.deleteVeterinaryVisitFromPet(petId, veterinaryVisitId)
                getVeterinaryVisitsFlow(petId)
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            } catch (e: Exception) {
                _errorState.value = "Error al eliminar la cita: ${e.message}"
            }
        }
    }
}