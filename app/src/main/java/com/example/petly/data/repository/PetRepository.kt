package com.example.petly.data.repository

import android.net.Uri
import com.example.petly.data.models.Pet
import com.example.petly.data.models.PetfromFirestoreMap
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.utils.CloudStorageManager
import com.example.petly.utils.toTimestamp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class PetRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val storage = CloudStorageManager()

    private fun userId() = auth.currentUser?.uid

    suspend fun addPetWithImage(pet: Pet, imageUri: Uri, fileName: String): String {
        if (userId() != null) {

            val petRef = firestore.collection("pets").document()
            val petId = petRef.id

            pet.id = petId
            pet.owners = listOf(userId().toString())

            val fileRef = Firebase.storage.reference
                .child("photos")
                .child("pets")
                .child(petId)
                .child(fileName)

            fileRef.putFile(imageUri).await()

            val imageUrl = fileRef.downloadUrl.await().toString()
            pet.photo= imageUrl

            petRef.set(pet.toFirestoreMap()).await()

            return petId
        } else {
            throw Exception("User not authenticated")
        }
    }

    suspend fun addPet(pet: Pet): String {
        if (userId() != null) {
            val petRef = firestore.collection("pets").document()
            val petId = petRef.id

            pet.id = petId
            pet.owners = listOf(userId().toString())

            pet.photo = "https://firebasestorage.googleapis.com/v0/b/petly-2d5c2.firebasestorage.app/o/photos%2Fpets%2Fdefault%2Fdefault_pet_profile_photo.jpg?alt=media&token=54986c7c-9707-4bb4-83fd-a87ca7855c6d"

            petRef.set(pet.toFirestoreMap()).await()

            return petId
        } else {
            throw Exception("User not authenticated")
        }
    }

    suspend fun updatePet(pet: Pet) {
        val petRef = pet.id.let {
            firestore.collection("pets").document(it!!)
        }
        petRef.set(pet.toFirestoreMap()).await()
    }

    suspend fun updateBasicData(
        petId: String,
        name: String,
        type: String,
        breed: String?,
        gender: String
    ){
        val petRef = firestore.collection("pets").document(petId)
        val updateMap = mapOf(
            "name" to name,
            "type" to type,
            "breed" to breed,
            "gender" to gender
        )
        petRef.update(updateMap).await()
    }

    suspend fun updateBirthdate(petId: String, birthDate: LocalDate?){
        val petRef = firestore.collection("pets").document(petId)
        val updateMap = mapOf(
            "birthDate" to birthDate?.toTimestamp()
        )
        petRef.update(updateMap).await()
    }

    suspend fun updateAdoptionDate(petId: String, adoptionDate: LocalDate?){
        val petRef = firestore.collection("pets").document(petId)
        val updateMap = mapOf(
            "adoptionDate" to adoptionDate?.toTimestamp()
        )
        petRef.update(updateMap).await()
    }

    suspend fun updateSterilizationInfo(petId: String, sterilized: Boolean, sterilizedDate: LocalDate?) {
        val petRef = firestore.collection("pets").document(petId)
        val updateMap = mapOf(
            "sterilized" to sterilized,
            "sterilizedDate" to sterilizedDate?.toTimestamp()
        )
        petRef.update(updateMap).await()
    }

    suspend fun updateMicrochipInfo(
        petId: String,
        microchipId: String,
        microchipDate: LocalDate?
    ) {
        val petRef = firestore.collection("pets").document(petId)
        val updateMap = mapOf(
            "microchipId" to microchipId,
            "microchipDate" to microchipDate?.toTimestamp()
        )
        petRef.update(updateMap).await()
    }

    suspend fun updatePetProfilePhoto(petId: String, newPhotoUri: Uri) {
        if (userId() != null) {
            try {
                val fileRef = FirebaseStorage.getInstance().reference
                    .child("photos")
                    .child("pets")
                    .child(petId)
                    .child("profile_pet_photo.jpg")

                fileRef.putFile(newPhotoUri).await()

                val imageUrl = fileRef.downloadUrl.await().toString()

                val petRef = firestore.collection("pets").document(petId)
                val petDoc = petRef.get().await()

                if (petDoc.exists()) {
                    val pet = PetfromFirestoreMap(petDoc.data ?: emptyMap())
                    pet.id = petId
                    pet.photo = imageUrl
                    petRef.set(pet.toFirestoreMap()).await()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Error al actualizar la foto de perfil de la mascota")
            }
        } else {
            throw Exception("User not authenticated")
        }
    }



    suspend fun deletePet(petId: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.delete().await()

        val weightsQuery = firestore.collection("weights")
            .whereEqualTo("petId", petId)
            .get()
            .await()

        for (doc in weightsQuery.documents) {
            doc.reference.delete().await()
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("photos/pets/$petId/")
        try {
            val files = storageRef.listAll().await()

            for (file in files.items) {
                file.delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPetsFlow(): Flow<List<Pet>> = callbackFlow {
        if (userId() != null) {
            val petRefs = firestore.collection("pets").whereArrayContains("owners", userId().toString())
            val subscription = petRefs.addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val pets = mutableListOf<Pet>()
                    for (document in querySnapshot.documents) {
                            val data = document.data
                            val pet = PetfromFirestoreMap(data ?: emptyMap())
                            pet.id = document.id
                            pet.let { pets.add(it)
                        }
                    }
                    trySend(pets).isSuccess
                }
            }
            awaitClose { subscription.remove() }
        } else {
            throw Exception("User not authenticated")
        }
    }

    suspend fun getPetById(petId: String): Pet? {
        return try {
            val petDoc = firestore.collection("pets").document(petId).get().await()
            val data = petDoc.data
            if (petDoc.exists()) {
                val pet = PetfromFirestoreMap(data ?: emptyMap())
                pet.id = petDoc.id
                pet
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
