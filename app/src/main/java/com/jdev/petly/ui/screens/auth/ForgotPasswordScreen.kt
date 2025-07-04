package com.jdev.petly.ui.screens.auth

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jdev.petly.R
import com.jdev.petly.ui.components.BaseOutlinedTextField
import com.jdev.petly.utils.AnalyticsManager
import com.jdev.petly.utils.AuthManager
import com.jdev.petly.viewmodel.PreferencesViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    navigateToLogin: () -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email: String by remember { mutableStateOf("") }
    val darkMode = preferencesViewModel.isDarkMode.collectAsState()

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BaseOutlinedTextField(
                value = email,
                placeHolder = "ejemplo@gmail.com",
                label = stringResource(R.string.email),
                leadingIcon = Icons.Default.Mail,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            ) {
                email = it
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    scope.launch {
                        auth.resetPasswordFlow(
                            email,
                            auth,
                            //analytics,
                            context,
                            navigateToLogin
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = stringResource(R.string.recover_password))
            }
        }
    }
}


