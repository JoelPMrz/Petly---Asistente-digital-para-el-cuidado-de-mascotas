package com.example.petly.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.Pet
import com.example.petly.data.models.PetEvent
import com.example.petly.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _eventsState = MutableStateFlow<List<PetEvent>>(emptyList())
    val eventsState: StateFlow<List<PetEvent>> get() = _eventsState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    fun observeEventsForPets(pets: List<Pet>, date: LocalDate) {
        val petIds = pets.map { it.id.toString() }
        viewModelScope.launch {
            try {
                eventRepository.getEventsForPetsByDate(petIds, date).collect { events ->
                    _eventsState.value = events
                }
            } catch (e: Exception) {
                _errorState.value = "Error loading events: ${e.message}"
            }
        }
    }
}
