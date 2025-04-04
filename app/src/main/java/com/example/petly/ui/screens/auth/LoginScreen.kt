package com.example.petly.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petly.R
import com.example.petly.navegation.ForgotPassword
import com.example.petly.navegation.Home
import com.example.petly.navegation.Login
import com.example.petly.navegation.SingUp
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.PasswordOutlinedTextField
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.AuthRes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(analytics: AnalyticsManager, auth: AuthManager, navigation: NavController) {
    val context = LocalContext.current
    var name: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (val account =
            auth.handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))) {
            is AuthRes.Success -> {
                val credential = GoogleAuthProvider.getCredential(account.data.idToken, null)
                scope.launch {
                    val fireUser = auth.signInWithGoogleCredential(credential)
                    if (fireUser != null) {
                        Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                        navigation.navigate(Home) {
                            popUpTo(Login) {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            is AuthRes.Error -> {
                analytics.logError("Error SignIn: ${account.errorMessage}")
                Toast.makeText(context, "Error: ${account.errorMessage}", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(context, "Error desconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = "Background login",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp, top = 120.dp, end = 30.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BaseOutlinedTextField(
                value = name,
                label = "Nombre",
                leadingIcon = Icons.Default.Person
            ) {
                name = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            BaseOutlinedTextField(
                value = email,
                placeHolder = "ejemplo@gmail.com",
                label = "Correo electrónico",
                leadingIcon = Icons.Default.Mail
            ) {
                email = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            PasswordOutlinedTextField(value = password) {
                password = it
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Forgot password?", modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        navigation.navigate(ForgotPassword)
                    },
                color = colorResource(R.color.blue80)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                ButtonSingIn {
                    scope.launch {
                        signInEmailPassword(email, password, auth, analytics, context, navigation)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        auth.signInWithGoogle(googleSignInLauncher)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue50))
                )
                {
                    Icon(
                        imageVector = Icons.Filled.Android,
                        contentDescription = "Android",
                        modifier = Modifier.padding(end = 10.dp),
                        tint = colorResource(R.color.blue80)
                    )
                    Text(text = "Google", fontSize = 15.sp, color = colorResource(R.color.blue80))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = {
                        scope.launch {
                            signInAnonymously(auth, analytics, context, navigation)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue50))
                )
                {
                    Icon(
                        imageVector = Icons.Default.NoAccounts,
                        contentDescription = "No accounts",
                        modifier = Modifier.padding(end = 10.dp),
                        tint = colorResource(R.color.blue80)
                    )
                    Text(text = "Invitado", fontSize = 15.sp, color = colorResource(R.color.blue80))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = "Already have an account?", color = colorResource(R.color.blue80))
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Sing up", modifier = Modifier.clickable {
                    navigation.navigate(SingUp)
                },
                    color = colorResource(R.color.blue100),
                    fontWeight = FontWeight.SemiBold
                )
            }


        }


    }

}

@Composable
fun ButtonSingIn(onSingIn: () -> Unit) {
    Button(
        onClick = {
            onSingIn()
        },
        modifier = Modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue100))
    )
    {
        Text(text = "INICIAR", fontSize = 16.sp, color = colorResource(id = R.color.white))
        Spacer(modifier = Modifier.width(7.dp))
        Icon(
            imageVector = Icons.Default.ArrowRightAlt,
            contentDescription = "ArrowForward icon",
            modifier = Modifier.size(30.dp),
            tint = colorResource(id = R.color.white)
        )
    }
}

@Composable
fun ButtonGoogleSingIn(onSingIn: () -> Unit) {
    Button(
        onClick = {
            onSingIn()
        },
    )
    {
        Icon(
            imageVector = Icons.Filled.Android,
            contentDescription = "Android",
            modifier = Modifier.padding(end = 10.dp)
        )
        Text(text = "Continuar con Google", fontSize = 14.sp)
    }
}

@Composable
fun ButtonSignInAnonymously(onSingIn: () -> Unit) {
    Button(
        onClick = {
            onSingIn()
        },
        modifier = Modifier.fillMaxWidth()
    )
    {
        Icon(
            imageVector = Icons.Default.NoAccounts,
            contentDescription = "No accounts",
            modifier = Modifier.padding(end = 10.dp)
        )
        Text(text = "Continuar como invitado", fontSize = 14.sp)
    }
}


private suspend fun signInAnonymously(
    auth: AuthManager,
    analytics: AnalyticsManager,
    context: Context,
    navigation: NavController
) {
    when (val result = auth.signInAnonymously()) {
        is AuthRes.Success -> {
            analytics.logButtonClicked("Click: Continuar como inviatdo")
            navigation.navigate(Home) {
                popUpTo(Login) {
                    inclusive = true
                }
            }

        }

        is AuthRes.Error -> {
            analytics.logError("Error SignIn Inconginito: ${result.errorMessage}")
        }
    }
}


private suspend fun signInEmailPassword(
    email: String,
    password: String,
    auth: AuthManager,
    analytics: AnalyticsManager,
    context: Context,
    navigation: NavController
) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        when (val result = auth.signInWithEmailPassword(email, password)) {
            is AuthRes.Success -> {
                analytics.logButtonClicked("Click: Iniciar sesión correo y contraseña")
                navigation.navigate(Home) {
                    popUpTo(Login) {
                        inclusive = true
                    }
                }
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