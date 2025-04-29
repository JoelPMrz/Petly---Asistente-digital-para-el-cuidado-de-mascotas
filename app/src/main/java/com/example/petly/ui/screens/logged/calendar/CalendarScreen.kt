package com.example.petly.ui.screens.logged.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.petly.ui.components.MyNavigationAppBar

@Composable
fun CalendarScreen(
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit
){
    Scaffold(
        bottomBar = { MyNavigationAppBar(navigateToHome,navigateToCalendar,navigateToUser, 0) }
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ){

        }

    }
}