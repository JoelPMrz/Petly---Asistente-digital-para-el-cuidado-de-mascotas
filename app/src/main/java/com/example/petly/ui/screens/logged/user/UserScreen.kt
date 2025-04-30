package com.example.petly.ui.screens.logged.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.petly.R
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.utils.AuthManager

@Composable
fun UserScreen(
    auth: AuthManager,
    navigateBack:()-> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit
){
    Scaffold(
        bottomBar = { MyNavigationAppBar(navigateToHome,navigateToCalendar,navigateToUser, 2) }
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ){
            Button(onClick = {
                auth.singOut()
                navigateBack()
            }) {
                Text(text = stringResource(R.string.sign_out))
            }
        }

    }
}