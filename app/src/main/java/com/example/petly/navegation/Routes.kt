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
object AddPet

@Serializable
data class PetDetail(
    val petId : String
)

@Serializable
object UserDetail

@Serializable
data class Weights(
    val petId : String
)

@Serializable
data class AddWeight(
    val petId : String
)

@Serializable
data class DetailWeight(
    val weightId : String
)
