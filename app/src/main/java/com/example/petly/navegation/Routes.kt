package com.example.petly.navegation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Home

@Serializable
object ForgotPassword

@Serializable
object SingUp

@Serializable
object CreatePet

@Serializable
data class PetDetail(
    val petId : String
)

@Serializable
object UserDetail
