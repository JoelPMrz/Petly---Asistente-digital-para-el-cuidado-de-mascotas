package com.example.petly.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.User
import com.example.petly.data.repository.UserRepository
import com.example.petly.viewmodel.UserViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val sigInClient = Identity.getSignInClient(context)

    suspend fun signInAnonymously(): AuthRes<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously().await()
            AuthRes.Success(result.user ?: throw Exception("Error al iniciar sesión"))
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    suspend fun createUserWithEmailPassword(email: String, password: String): AuthRes<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return AuthRes.Error("Usuario nulo después del registro")
            AuthRes.Success(user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al crear el usuario")
        }
    }


    suspend fun signInWithEmailPassword(email: String, password: String): AuthRes<FirebaseUser?>{
        return try{
           val authResult = auth.signInWithEmailAndPassword(email, password).await()
            AuthRes.Success(authResult.user)

        }catch (e : Exception){
            AuthRes.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    @Suppress("DEPRECATION")
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }


    fun handleSignInResult(task: Task<GoogleSignInAccount>): AuthRes<GoogleSignInAccount>?{
        return try{
            val account = task.getResult(ApiException::class.java)
            AuthRes.Success(account)
        }catch (e: Exception){
            AuthRes.Error(e.message ?: "Google sign-in failed")
        }
    }

    suspend fun signInWithGoogleCredential(credential: AuthCredential) : AuthRes<FirebaseUser>?{
        return try{
            val firebaseUser = auth.signInWithCredential(credential).await()
            firebaseUser.user?.let {
                AuthRes.Success(it)
            }
        }catch(e: Exception){
            AuthRes.Error(e.message ?: "Sign in with Google failed")
        }
    }

    fun signInWithGoogle(googleSignInLauncher : ActivityResultLauncher<Intent>){
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    suspend fun signInAnonymously(
        auth: AuthManager,
        analytics: AnalyticsManager,
        navigateToHome: () -> Unit
    ) {
        when (val result = auth.signInAnonymously()) {
            is AuthRes.Success -> {
                analytics.logButtonClicked("Click: Continuar como inviatdo")
                navigateToHome()

            }

            is AuthRes.Error -> {
                analytics.logError("Error SignIn Inconginito: ${result.errorMessage}")
            }
        }
    }

    suspend fun signInEmailPassword(
        email: String,
        password: String,
        auth: AuthManager,
        analytics: AnalyticsManager,
        context: Context,
        navigateToHome: () -> Unit
    ) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            when (val result = auth.signInWithEmailPassword(email, password)) {
                is AuthRes.Success -> {
                    analytics.logButtonClicked("Click: Iniciar sesión correo y contraseña")
                    navigateToHome()
                }

                is AuthRes.Error -> {
                    analytics.logButtonClicked("Error SignIn: ${result.errorMessage}")
                    Toast.makeText(context, "Error SignIn: ${result.errorMessage}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_SHORT).show()
        }
    }



    suspend fun resetPassword(email: String): AuthRes<Unit?>{
        return try{
            auth.sendPasswordResetEmail(email).await()
            AuthRes.Success(Unit)
        }catch (e : Exception){
            AuthRes.Error(e.message ?: "Error al restablecer la contraseña")
        }
    }

    fun singOut(){
        auth.signOut()
        sigInClient.signOut()
    }

    fun getCurrentUser(): FirebaseUser?{
        return auth.currentUser
    }
}