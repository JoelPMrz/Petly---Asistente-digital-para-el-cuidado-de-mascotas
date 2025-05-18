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
data class VeterinaryVisits(
    val petId : String
)

@Serializable
data class Owners(
    val petId : String
)

@Serializable
data class Observers(
    val petId : String
)


