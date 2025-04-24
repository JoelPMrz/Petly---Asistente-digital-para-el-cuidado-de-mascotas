package com.example.petly.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class CloudStorageManager() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    fun getStorageReference(petId: String): StorageReference {
        return storageRef.child("photos").child("pets").child(petId)
    }

    suspend fun uploadFile(fileName: String, filePath: Uri, petId: String): String {
        val fileRef = Firebase.storage.reference
            .child("photos")
            .child("pets")
            .child(petId)
            .child(fileName)

        fileRef.putFile(filePath).await()
        // Obtener la URL de la imagen subida
        return fileRef.downloadUrl.await().toString()
    }
}
