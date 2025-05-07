package com.example.petly.utils

import android.content.Context
import android.widget.Toast
import java.io.File

fun clearAppCache(context: Context) {
    try {
        val dir: File = context.cacheDir
        if (dir.exists() && dir.isDirectory) {
            val children: Array<String> = dir.list() ?: arrayOf()
            var errors = false
            for (child in children) {
                val file = File(dir, child)
                if (file.exists() && file.isFile) {
                    val success = file.delete()
                    if (!success) {
                        errors = true
                    }
                }
            }
            if (errors) {
                Toast.makeText(context, "No se ha podido limpiar todo el caché", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Caché limpiado", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No se encontró el directorio de caché", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {

        Toast.makeText(context, "Hubo un error al intentar limpiar el caché", Toast.LENGTH_SHORT).show()
    }
}