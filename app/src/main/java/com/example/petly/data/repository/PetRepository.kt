package com.example.petly.data.repository

import android.net.Uri
import com.example.petly.data.models.Pet
import com.example.petly.data.models.petfromFirestoreMap
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.utils.FirebaseConstants.DEFAULT_PET_PHOTO_URL
import com.example.petly.utils.toTimestamp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class PetRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun userId() = auth.currentUser?.uid

    suspend fun addPet(pet: Pet): String {
        if (userId() != null) {
            val petRef = firestore.collection("pets").document()
            val petId = petRef.id

            pet.id = petId
            pet.creatorOwner = userId().toString()
            pet.owners = listOf(userId().toString())
            pet.photo = DEFAULT_PET_PHOTO_URL
            pet.createdAt = LocalDateTime.now()


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

    suspend fun updatePetProfilePhoto(petId: String, newPhotoUri: Uri): String {
        val uid = userId() ?: throw Exception("User not authenticated")

        val petRef = firestore.collection("pets").document(petId)
        val petDoc = petRef.get().await()

        if (!petDoc.exists()) {
            throw Exception("Mascota no encontrada")
        }

        val data = petDoc.data
        val creatorOwner = data?.get("creatorOwner") as? String
        val owners = data?.get("owners") as? List<*>

        val isAuthorized = creatorOwner == uid || (owners?.contains(uid) == true)
        if (!isAuthorized) {
            throw Exception("No permission to update photo")
        }

        val fileRef = FirebaseStorage.getInstance().reference
            .child("photos")
            .child("pets")
            .child(petId)
            .child("profile_pet_photo.jpg")

        fileRef.putFile(newPhotoUri).await()
        val imageUrl = fileRef.downloadUrl.await().toString()

        petRef.update("photo", imageUrl).await()

        return imageUrl
    }

    suspend fun addPetOwner(petId: String, userIdToAdd: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("owners", FieldValue.arrayUnion(userIdToAdd)).await()
    }

    suspend fun addPetObserver(petId: String, userIdToAdd: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("observers", FieldValue.arrayUnion(userIdToAdd)).await()
    }

    suspend fun updatePetCreatorOwner(petId: String, newCreatorId: String){
        val petRef = firestore.collection("pets").document(petId)
        val updateMap = mapOf(
            "creatorOwner" to newCreatorId,
        )
        petRef.update(updateMap).await()
    }

    suspend fun deletePetOwner(petId: String, userIdToRemove: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("owners", FieldValue.arrayRemove(userIdToRemove)).await()
    }

    suspend fun deletePetObserver(petId: String, userIdToRemove: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("observers", FieldValue.arrayRemove(userIdToRemove)).await()
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

        val veterinaryVisitsQuery = firestore.collection("veterinaryVisits")
            .whereEqualTo("petId", petId)
            .get()
            .await()

        for (doc in veterinaryVisitsQuery.documents){
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

    fun getOwnerPetsFlow(): Flow<List<Pet>> = callbackFlow {
        if (userId() != null) {
            val petRefs = firestore.collection("pets").whereArrayContains("owners", userId().toString())
            val subscription = petRefs.addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val pets = mutableListOf<Pet>()
                    for (document in querySnapshot.documents) {
                            val data = document.data
                            val pet = petfromFirestoreMap(data ?: emptyMap())
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

    fun getObservedPetsFlow(): Flow<List<Pet>> = callbackFlow {
        if (userId() != null) {
            val petRefs = firestore.collection("pets").whereArrayContains("observers", userId().toString())
            val subscription = petRefs.addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val pets = mutableListOf<Pet>()
                    for (document in querySnapshot.documents) {
                        val data = document.data
                        val pet = petfromFirestoreMap(data ?: emptyMap())
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


    fun getAllUserPetsFlow(): Flow<List<Pet>> = combine(
        getOwnerPetsFlow(),
        getObservedPetsFlow()
    ) { ownedPets, observedPets ->
        (ownedPets + observedPets).distinctBy { it.id }
    }

    suspend fun getPetById(petId: String): Pet? {
        return try {
            val petDoc = firestore.collection("pets").document(petId).get().await()
            val data = petDoc.data
            if (petDoc.exists()) {
                val pet = petfromFirestoreMap(data ?: emptyMap())
                pet.id = petDoc.id
                pet
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getPetFlowById(petId: String): Flow<Pet?> = callbackFlow {
        val docRef = firestore.collection("pets").document(petId)
        val listener = docRef.addSnapshotListener { snapshot, _ ->
            val data = snapshot?.data
            if (snapshot != null && snapshot.exists()) {
                val pet = petfromFirestoreMap(data ?: emptyMap())
                pet.id = snapshot.id
                trySend(pet).isSuccess
            } else {
                trySend(null).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    fun getPetsFlowByList(pets: List<Pet>): Flow<List<Pet>> = callbackFlow {
        if (pets.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val petsMap = mutableMapOf<String, Pet>()
        val listeners = pets.mapNotNull { pet ->
            val petId = pet.id ?: return@mapNotNull null

            firestore.collection("pets").document(petId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        val data = snapshot.data
                        val updatedPet = petfromFirestoreMap(data ?: emptyMap())
                        updatedPet.id = snapshot.id

                        petsMap[updatedPet.id!!] = updatedPet

                        // Emitimos la lista actualizada de mascotas
                        trySend(petsMap.values.toList()).isSuccess
                    }
                }
        }

        // Cuando se cierre el flow, eliminar listeners
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }


}
