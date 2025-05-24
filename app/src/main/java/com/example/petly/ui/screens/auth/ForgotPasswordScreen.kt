package com.example.petly.ui.screens.auth

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.petly.R
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import kotlinx.coroutines.launch

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
            label = stringResource(R.string.email),
            leadingIcon = Icons.Default.Mail,
            maxLines = 1
        ) {
            email = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            scope.launch {
                auth.resetPasswordFlow(
                    email,
                    auth,
                    //analytics,
                    context,
                    navigateToLogin)
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.recover_password))
        }
    }
}


