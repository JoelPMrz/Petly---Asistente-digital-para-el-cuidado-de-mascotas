package com.example.petly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petly.data.models.User
import com.example.petly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
):ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> get() = _userState

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
                onFailure(e)
            }
        }
    }

    fun getUserById(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId)
                _userState.value = user
            } catch (e: Exception) {
                _userState.value = null
            }
        }
    }

    fun clearUser() {
        _userState.value = null
    }
}