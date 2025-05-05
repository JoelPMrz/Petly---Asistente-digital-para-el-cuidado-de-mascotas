package com.example.petly.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.PasswordOutlinedTextField
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.AuthRes
import com.example.petly.viewmodel.UserViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

@Composable
fun SingUpScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    navigateBack:()->Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    var password: String by remember { mutableStateOf("") }
    var name: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BaseOutlinedTextField(
            value = name,
            label = "Nombre",
            leadingIcon = Icons.Default.Person,
            maxLines = 1
        ) {
            name = it
        }
        Spacer(modifier = Modifier.height(20.dp))
        BaseOutlinedTextField(
            value = email,
            placeHolder = "ejemplo@gmail.com",
            label = "Correo electrónico",
            leadingIcon = Icons.Default.Mail,
            maxLines = 1
        ) {
            email = it
        }
        Spacer(modifier = Modifier.height(20.dp))
        PasswordOutlinedTextField(value = password) {
            password = it
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            scope.launch {
                signUp(email, name, password, auth, analytics, navigateBack, context, userViewModel)
            }
        }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
            Text(text = "Registrarse")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Ya tienes cuenta? Iniciar sesión", modifier = Modifier.clickable { navigateBack() })
    }

}

private suspend fun signUp(
    email: String,
    name : String?,
    password: String,
    auth: AuthManager,
    analytics: AnalyticsManager,
    navigateBack:()->Unit,
    context: Context,
    userViewModel: UserViewModel
) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        when(val result = auth.createUserWithEmailPassword(email,password)){
            is AuthRes.Success->{
                analytics.logButtonClicked(FirebaseAnalytics.Event.SIGN_UP)
                userViewModel.addUser(
                    name,
                    email,
                    onSuccess = {
                        navigateBack()
                        Toast.makeText(context, "Registro completado", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {

                    },
                    alreadyExist = {

                    }
                )
            }
            is AuthRes.Error->{
                analytics.logButtonClicked("Error SignUp: ${result.errorMessage}")
                Toast.makeText(context, "Error SignUp: ${result.errorMessage}", Toast.LENGTH_SHORT ).show()
            }
        }
    } else {
        Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_SHORT).show()
    }
}



