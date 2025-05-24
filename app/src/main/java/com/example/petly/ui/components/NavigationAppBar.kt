package com.example.petly.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable


@Composable
fun MyNavigationAppBar(
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    index : Int
) {

    NavigationBar {
        NavigationBarItem(
            selected = index == 0,
            onClick = {
                navigateToCalendar()
            },
            icon = {
                Icon(
                    imageVector = if (index != 0) Icons.Outlined.CalendarMonth else Icons.Rounded.CalendarMonth,
                    contentDescription = "Favourite icon",
                    // tint = if (index != 0) Color.LightGray else Color.Gray
                )
            },
            alwaysShowLabel = false,
            //colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent,)
        )

        NavigationBarItem(
            selected = index == 1,
            onClick = {
                navigateToHome()
                      },
            icon = {
                Icon(
                    imageVector = if (index != 1) Icons.Outlined.Home else Icons.Rounded.Home,
                    contentDescription = "Home icon",
                    //tint = if (index != 1) Color.LightGray else Color.Gray
                )
            },
            alwaysShowLabel = false,
            //colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )

        NavigationBarItem(
            selected = index == 2,
            onClick = {
                navigateToUser()
                      },
            icon = {
                Icon(
                    imageVector = if (index != 2) Icons.Outlined.Person else Icons.Rounded.Person,
                    contentDescription = "Search icon",
                    //tint =  if (index != 2) Color.LightGray else Color.Gray
                )
            },
            alwaysShowLabel = false,
            //colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )

    }
}