package com.example.petly.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.User
import com.example.petly.data.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.storage.StorageException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
):ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> get() = _userState


    fun getUserFlowById(userId: String) {
        viewModelScope.launch {
            userRepository.getUserByIdFlow(userId).collect { user ->
                _userState.value = user
            }
        }
    }


    private val _createdOwnerState = MutableStateFlow<User?>(null)
    val createdOwnerState: StateFlow<User?> get() = _createdOwnerState

    fun getFlowCreatedOwner(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.getUserByIdFlow(userId).collect { user ->
                    _createdOwnerState.value = user
                }
            } catch (e: Exception) {
                _createdOwnerState.value = null
            }
        }
    }

    fun checkUserExists(
        userId: String,
        onSuccess: () -> Unit,
        notExist: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId)
                if(user != null){
                    onSuccess()
                }else{
                    notExist()
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    private val _ownersState = MutableStateFlow<List<User>>(emptyList())
    val ownersState: StateFlow<List<User>> get() = _ownersState

    private val _observersState = MutableStateFlow<List<User>>(emptyList())
    val observersState: StateFlow<List<User>> get() = _observersState

    fun getUsersByRole(petId: String, roleField: String) {
        viewModelScope.launch {
            try {
                userRepository.getUsersFromPetByRoleFlow(petId, roleField).collect { users ->
                    when (roleField) {
                        "owners" -> if (_ownersState.value != users) _ownersState.value = users
                        "observers" -> if (_observersState.value != users) _observersState.value = users
                    }
                }
            } catch (e: Exception) {
                //
            }
        }
    }


    fun addUser(
        name: String?,
        email: String,
        photo: String? = null,
        alreadyExist:() ->Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try {
                userRepository.addUser(name, email, photo, alreadyExist, onSuccess)
            }catch (e : Exception){
                withContext(Dispatchers.Main) {
                    when (e) {
                        is FirebaseNetworkException -> {
                            onFailure(Exception("Sin conexión a Internet."))
                        }
                        is FirebaseAuthException -> {
                            onFailure(Exception("Error de autenticación."))
                        }
                        else -> {
                            onFailure(Exception("Error desconocido: ${e.message}"))
                        }
                    }
                }
            }
        }
    }

    private var uploadJob: Job? = null

    fun updateUserProfilePhoto(
        userId: String,
        newPhotoUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        uploadJob?.cancel()
        uploadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val newPhotoUrl = userRepository.updateUserProfilePhoto(userId, newPhotoUri)
                withContext(Dispatchers.Main) {
                    _userState.value?.let {
                        _userState.value = it.copy(photo = newPhotoUrl.toString())
                    }
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    when (e) {
                        is FirebaseNetworkException -> {
                            onFailure(Exception("Sin conexión a Internet."))
                        }
                        is FirebaseAuthException -> {
                            onFailure(Exception("Error de autenticación."))
                        }
                        is StorageException -> {
                            if (e.errorCode == StorageException.ERROR_NOT_AUTHORIZED) {
                                onFailure(Exception("No tienes permisos para subir la imagen."))
                            } else {
                                onFailure(Exception("Error de almacenamiento: ${e.message}"))
                            }
                        }
                        else -> {
                            onFailure(Exception("Error desconocido: ${e.message}"))
                        }
                    }
                }
            }
        }
    }

    fun updateUserBasicData(
        userId: String,
        name: String?
    ){
        viewModelScope.launch {
            try {
                userRepository.updateUserBasicData(userId, name)
            }catch(e : Exception){

            }
        }
    }


    fun clearUser() {
        _userState.value = null
    }
}