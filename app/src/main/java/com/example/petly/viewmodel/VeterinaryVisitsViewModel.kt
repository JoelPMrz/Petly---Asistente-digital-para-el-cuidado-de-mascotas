package com.example.petly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.repository.VeterinaryVisitsRepository
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

    fun updateVeterinaryVisit(veterinaryVisit: VeterinaryVisit, notPermission : () -> Unit, onFailure : () -> Unit){
        viewModelScope.launch {
            try {
                veterinaryVisitsRepository.updateVeterinaryVisit(veterinaryVisit)
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

    fun deleteVeterinaryVisit(){
        viewModelScope.launch {
            try {

            }catch (e: Exception){

            }
        }
    }

}