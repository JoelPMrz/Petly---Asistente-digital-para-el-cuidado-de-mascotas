package com.jdev.petly.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jdev.petly.R
import com.jdev.petly.ui.components.BaseOutlinedTextField
import com.jdev.petly.ui.components.PasswordOutlinedTextField
import com.jdev.petly.utils.AnalyticsManager
import com.jdev.petly.utils.AuthManager
import com.jdev.petly.utils.AuthRes
import com.jdev.petly.utils.FirebaseConstants.DEFAULT_USER_PHOTO_URL
import com.jdev.petly.utils.mailValidated
import com.jdev.petly.utils.passwordValidated
import com.jdev.petly.viewmodel.PreferencesViewModel
import com.jdev.petly.viewmodel.UserViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

@Composable
fun SingUpScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    navigateBack: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    var password: String by remember { mutableStateOf("") }
    var isErrorPassword by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }
    var wasPasswordTouched by remember { mutableStateOf(false) }

    var name: String by remember { mutableStateOf("") }
    var isErrorName by remember { mutableStateOf(false) }
    val nameFocusRequester = remember { FocusRequester() }
    var wasNameTouched by remember { mutableStateOf(false) }

    var email: String by remember { mutableStateOf("") }
    var isErrorMail by remember { mutableStateOf(false) }
    val mailFocusRequester = remember { FocusRequester() }
    var wasMailTouched by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val darkMode = preferencesViewModel.isDarkMode.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = if (darkMode.value) painterResource(R.drawable.dark_login) else painterResource(
                R.drawable.login_background
            ),
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
                label = stringResource(R.string.name),
                leadingIcon = Icons.Default.Person,
                isRequired = true,
                maxLength = 25,
                maxLines = 1,
                isError = isErrorName,
                keyboardActions = KeyboardActions(
                    onNext = {
                        mailFocusRequester.requestFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .focusRequester(nameFocusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            wasNameTouched = true
                        } else {
                            if (wasNameTouched) {
                                isErrorName = name.isBlank()
                            }
                        }
                    }
            ) {
                name = it
                if(wasNameTouched && it.isNotBlank()){
                    isErrorName = false
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            BaseOutlinedTextField(
                value = email,
                modifier = Modifier
                    .focusRequester(mailFocusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            wasMailTouched = true
                        }else{
                            if (wasMailTouched) {
                                isErrorMail = !mailValidated(email)
                            }
                        }
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFocusRequester.requestFocus()
                    }
                ),
                placeHolder = "example@gmail.com",
                label = stringResource(R.string.email),
                leadingIcon = Icons.Default.Mail,
                isRequired = true,
                isError = isErrorMail,
                maxLines = 1
            ) {
                email = it
                if(wasMailTouched && !mailValidated(it)){
                    isErrorMail = false
                }
            }
            AnimatedVisibility(isErrorMail){
                Text(text = stringResource(R.string.invalid_email), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            PasswordOutlinedTextField(
                value = password,
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            wasPasswordTouched = true
                        }else{
                            if (wasPasswordTouched) {
                                isErrorPassword = !passwordValidated(password)
                            }
                        }
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                isError = isErrorPassword
            ) {
                password = it
                if(wasPasswordTouched && !passwordValidated(it)){
                    isErrorPassword = false
                }
            }
            AnimatedVisibility(isErrorPassword){
                Text(text = stringResource(R.string.passwordInvalid), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    scope.launch {
                        signUp(
                            email,
                            name,
                            password,
                            auth,
                            analytics,
                            navigateBack,
                            context,
                            userViewModel
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = stringResource(R.string.register))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.already_have_an_account),
                modifier = Modifier.clickable { navigateBack() },
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private suspend fun signUp(
    email: String,
    name: String?,
    password: String,
    auth: AuthManager,
    analytics: AnalyticsManager,
    navigateBack: () -> Unit,
    context: Context,
    userViewModel: UserViewModel
) {
    if (mailValidated(email) && passwordValidated(password) && !name.isNullOrBlank()) {
        when (val result = auth.createUserWithEmailPassword(email, password)) {
            is AuthRes.Success -> {
                analytics.logButtonClicked(FirebaseAnalytics.Event.SIGN_UP)
                userViewModel.addUser(
                    name,
                    email,
                    photo = DEFAULT_USER_PHOTO_URL,
                    onSuccess = {
                        navigateBack()
                        Toast.makeText(
                            context,
                            context.getString(R.string.registered_account),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFailure = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.an_error_has_occurred),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    alreadyExist = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.account_already_exists),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            is AuthRes.Error -> {
                analytics.logButtonClicked("Error SignUp: ${result.errorMessage}")
                Toast.makeText(
                    context,
                    result.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } else {
        if(!mailValidated(email)){
            Toast.makeText(
                context,
                context.getString(R.string.invalid_email),
                Toast.LENGTH_SHORT
            ).show()
        }else if(!passwordValidated(password)){
            Toast.makeText(
                context,
                context.getString(R.string.passwordInvalid),
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            Toast.makeText(
                context,
                context.getString(R.string.all_fields_are_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}



