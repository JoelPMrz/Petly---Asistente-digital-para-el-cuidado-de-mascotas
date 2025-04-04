package com.example.petly.utils

import android.content.Context
import com.example.petly.data.models.PetModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeManager (context : Context) {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("pets")
    private val authManager = AuthManager(context)

    fun addPet(petModel : PetModel){
        val key = databaseReference.push().key
        if(key != null){
            databaseReference.child(key).setValue(petModel)
        }
    }

    fun deletePet(petId: String){
        databaseReference.child(petId).removeValue()
    }

    fun updatePet(petId: String, updatePetModel: PetModel){
        databaseReference.child(petId).setValue(updatePetModel)
    }

    fun getPetsFlows(): Flow<List<PetModel>> {
        val idFilter = authManager.getCurrentUser()?.uid
        val flow = callbackFlow {
            val listener  = databaseReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pets = snapshot.children.mapNotNull { snapshot ->
                        val petModel = snapshot.getValue(PetModel::class.java)
                        snapshot.key?.let{ petModel?.copy(key= it)}
                    }
                    trySend(pets.filter {  it.ownerId == idFilter }).isSuccess
                }
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose{ databaseReference.removeEventListener(listener)}
        }
        return flow
    }
}