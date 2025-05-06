package com.example.petly.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.petly.navegation.Login
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.AuthRes
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ForgotPasswordScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    navigateToLogin:()-> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email: String by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BaseOutlinedTextField(
            value = email,
            placeHolder = "ejemplo@gmail.com",
            label = "Correo electrónico",
            leadingIcon = Icons.Default.Mail,
            maxLines = 1
        ) {
            email = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            scope.launch {
                resetPassword(email, auth, analytics, context, navigateToLogin)
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Recuperar contraseña")
        }
    }
}

suspend fun resetPassword(
    email: String,
    auth: AuthManager,
    analytics: AnalyticsManager,
    context: Context,
    navigateToLogin: ()-> Unit
) {
    when (val res = auth.resetPassword(email)) {
        is AuthRes.Success -> {
            analytics.logButtonClicked(buttonName = "Reset password $email")
            Toast.makeText(context, "Correo enviado a $email", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        is AuthRes.Error -> {
            analytics.logError(error = "Reset password error: $email: ${res.errorMessage}")
            Toast.makeText(context, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
        }
    }
}

suspend fun changePassword(
    currentPassword: String,
    newPassword: String,
    auth: AuthManager,
    context: Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val user = auth.getCurrentUser()
    val email = user?.email

    if (user != null && !email.isNullOrEmpty()) {
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        try {
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
            onSuccess()
        } catch (e: Exception) {
            onError("Error: ${e.localizedMessage}")
        }
    } else {
        onError("Usuario no autenticado")
    }
}
