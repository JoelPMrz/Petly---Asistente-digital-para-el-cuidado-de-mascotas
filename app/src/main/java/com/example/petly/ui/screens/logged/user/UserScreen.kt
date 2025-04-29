package com.example.petly.ui.screens.logged.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.petly.ui.components.MyNavigationAppBar

@Composable
fun UserScreen(
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

        }

    }
}