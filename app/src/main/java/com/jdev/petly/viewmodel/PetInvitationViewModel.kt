package com.jdev.petly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdev.petly.data.models.PetInvitation
import com.jdev.petly.data.repository.PetInvitationRepository
import com.jdev.petly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PetInvitationViewModel @Inject constructor(
    private val petInvitationRepository: PetInvitationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _petInvitationsState = MutableStateFlow<List<PetInvitation>>(emptyList())
    val petInvitationsState: StateFlow<List<PetInvitation>> get() = _petInvitationsState

    private val _petInvitationState = MutableStateFlow<PetInvitation?>(null)
    val petInvitationState: StateFlow<PetInvitation?> get() = _petInvitationState

    fun getPetInvitations(){
        viewModelScope.launch {
            try {
                petInvitationRepository.getPetInvitationsFlow().collect{ petInvitations ->
                    if(_petInvitationsState.value != petInvitations){
                        _petInvitationsState.value = petInvitations
                    }
                }

            }catch (e: Exception){
                //
            }
        }
    }

    fun getPetInvitationById(
        petInvitationId: String
    ){
        viewModelScope.launch {
            try{
                val petInvitation = petInvitationRepository.getPetInvitationById(petInvitationId)
                _petInvitationState.value = petInvitation
            }catch (e: Exception){
                _petInvitationState.value = null
            }
        }
    }

     fun addPetInvitation(
        petId: String,
        petName: String,
        fromUserId : String,
        fromUserName: String,
        toUserId: String,
        role: String,
        onUserNotFound: () -> Unit,
        onInvitationSent: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(toUserId)
                if (user != null) {
                    petInvitationRepository.addPetInvitation(petId, petName, fromUserId, fromUserName, toUserId, role)
                    withContext(Dispatchers.Main) { onInvitationSent() }
                } else {
                    withContext(Dispatchers.Main) { onUserNotFound() }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    fun deletePetInvitation(
        petInvitationId: String,
        onSuccess:()-> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try {
                petInvitationRepository.deletePetInvitation(petInvitationId)
                onSuccess()
            }catch (e: Exception){
                onFailure(e)
            }
        }
    }
}