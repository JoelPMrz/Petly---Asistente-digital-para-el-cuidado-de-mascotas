package com.jdev.petly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdev.petly.data.models.Event
import com.jdev.petly.data.repository.NormalEventRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NormalEventViewModel @Inject constructor(
    private val normalEventRepository: NormalEventRepository
): ViewModel(){

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _event = MutableStateFlow<Event?> (null)
    val event : StateFlow<Event?>  get() = _event

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events : StateFlow<List<Event>> get() = _events


    fun getEventsFlow(
        petId: String
    ){
        viewModelScope.launch {
            try {
                normalEventRepository.getEventsFlow(
                    petId = petId
                ).collect { events ->
                    _events.value = events
                }
            }catch (e: Exception){
                _errorState.value = "Error al obtener las visitas veterinarias: ${e.message}"
            }
        }
    }

    fun addEvent(petId: String, event: Event, notPermission : () -> Unit, onFailure : () -> Unit){
        viewModelScope.launch {
            try {
                normalEventRepository.addEventToPet(petId ,event)
            } catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            }catch (e: Exception) {
                onFailure()
                _errorState.value = "Error al añadir evento: ${e.message}"
            }
        }
    }

    fun updateEvent(event: Event, eventNotExist:()-> Unit, notPermission : () -> Unit, onFailure : () -> Unit){
        viewModelScope.launch {
            try {
                val existingEvent = normalEventRepository.getEventById(event.id)
                if(existingEvent != null){
                    normalEventRepository.updateEvent(event)
                }else{
                    eventNotExist()
                }
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            } catch (e: Exception) {
                onFailure()
                _errorState.value = "Error al actualizar evento: ${e.message}"
            }
        }
    }

    fun deleteEvent(petId: String, eventId: String, notPermission: () -> Unit){
        viewModelScope.launch {
            try {
                normalEventRepository.deleteEvent(petId, eventId)
                getEventsFlow(petId)
            }catch (e: FirebaseFirestoreException) {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    notPermission()
                }
            } catch (e: Exception) {
                _errorState.value = "Error al eliminar el evento: ${e.message}"
            }
        }
    }
}