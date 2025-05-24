package com.example.petly.ui.screens.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.PasswordOutlinedTextField
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.AuthRes
import com.example.petly.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    navigateToHome:()-> Unit,
    navigateToForgotPassword:()-> Unit,
    navigateToSingUp:()-> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
                        userViewModel.addUser(
                            name = account.data.displayName,
                            email = account.data.email ?: "",
                            photo = account.data.photoUrl.toString(),
                            onSuccess = {
                                Toast.makeText(context,
                                    context.getString(R.string.welcome,account.data.displayName.toString() ) , Toast.LENGTH_SHORT).show()
                                navigateToHome()
                            },
                            onFailure = {
                                Toast.makeText(context, context.getString(R.string.error_creating_user), Toast.LENGTH_SHORT).show()
                            },
                            alreadyExist = {
                                Toast.makeText(context, context.getString(R.string.welcome_back, account.data.displayName.toString()), Toast.LENGTH_SHORT).show()
                                navigateToHome()
                            }
                        )
                    }
                }
            }

            is AuthRes.Error -> {
                analytics.logError("Error SignIn: ${account.errorMessage}")
                Toast.makeText(context, context.getString(R.string.an_error_has_occurred), Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(context, context.getString(R.string.an_error_has_occurred), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = if(isSystemInDarkTheme()) painterResource(R.drawable.dark_login) else painterResource(R.drawable.login_background),
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
            Spacer(modifier = Modifier.height(20.dp))
            BaseOutlinedTextField(
                value = email,
                placeHolder = "example@gmail.com",
                label = stringResource(R.string.email),
                leadingIcon = Icons.Default.Mail,
                maxLines = 1
            ) {
                email = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            PasswordOutlinedTextField(value = password) {
                password = it
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.forgot_password), modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        navigateToForgotPassword()
                    },
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                ButtonSingIn {
                    scope.launch {
                        auth.signInEmailPassword(email, password, auth, analytics, context, navigateToHome)
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                )
                {
                    Icon(
                        painter = painterResource(R.drawable.ic_google_24),
                        contentDescription = "Android",
                        modifier = Modifier.padding(end = 10.dp).size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(text = "Google", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = {
                        scope.launch {
                            auth.signInAnonymously(auth, analytics, navigateToHome)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                )
                {
                    Icon(
                        painter = painterResource(R.drawable.ic_incognito),
                        contentDescription = "No accounts",
                        modifier = Modifier.padding(end = 10.dp).size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(text = stringResource(R.string.guest), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = stringResource(R.string.not_create_account), fontSize = 15.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = stringResource(R.string.create_account), modifier = Modifier.clickable {
                        navigateToSingUp()
                },
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
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
    )
    {
        Text(text = stringResource(R.string.loggin), fontSize = 16.sp)
        Spacer(modifier = Modifier.width(7.dp))
        Icon(
            imageVector = Icons.Default.ArrowRightAlt,
            contentDescription = "ArrowForward icon",
            modifier = Modifier.size(30.dp)
        )
    }
}




