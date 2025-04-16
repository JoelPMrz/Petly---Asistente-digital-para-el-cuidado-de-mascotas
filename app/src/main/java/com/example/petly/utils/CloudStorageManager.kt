package com.example.petly.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class CloudStorageManager(context : Context) {
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val authManager = AuthManager(context)

    fun getStorageReference(petId: String): StorageReference{
        return storageRef.child("photos").child(petId)
    }

    suspend fun uploadFile(fileName: String, filePath: Uri, petId: String){
        val fileRef = getStorageReference(petId).child(fileName)
        val uploadTask = fileRef.putFile(filePath)
        uploadTask.await()
    }

    suspend fun getPetImages(petId: String): List<String>{
        val imageUrls = mutableListOf<String>()
        val listResult: ListResult = getStorageReference(petId).listAll().await()
        for (item in listResult.items){
            val url = item.downloadUrl.await().toString()
            imageUrls.add(url)
        }
        return imageUrls
    }
}