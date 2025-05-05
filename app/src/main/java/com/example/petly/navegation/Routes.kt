package com.example.petly.navegation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object ForgotPassword

@Serializable
object SingUp

@Serializable
object NavigationAppBar

@Serializable
object Home

@Serializable
object Calendar

@Serializable
object User

@Serializable
object AddPet

@Serializable
data class PetDetail(
    val petId : String
)


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
    val petId : String,
    val weightId : String
)
