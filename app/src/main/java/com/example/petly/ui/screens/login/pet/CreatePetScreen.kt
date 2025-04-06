package com.example.petly.ui.screens.login.pet

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petly.data.models.Pet
import com.example.petly.navegation.CreatePet
import com.example.petly.navegation.Home
import com.example.petly.navegation.Login
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CreatePetScreen(
    analytics: AnalyticsManager,
    navigateBack:() -> Unit,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var name: String by remember { mutableStateOf("") }
    var type: String by remember { mutableStateOf("") }
    var gender: String by remember { mutableStateOf("") }
    var enableCreatePet: Boolean by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            MyTopAppBar(
                {
                    navigateBack()
                }
            )
        },
        bottomBar = { MyNavigationAppBar() },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = { MyFloatingActionButton() },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BaseOutlinedTextField(
                value = name,
                label = "Nombre",
                leadingIcon = Icons.Default.Pets
            ) {
                name = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            BaseOutlinedTextField(
                value = type,
                placeHolder = "Perro",
                label = "Tipo",
                leadingIcon = Icons.Default.AutoMode
            ) {
                type = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            BaseOutlinedTextField(
                value = gender,
                placeHolder = "Macho",
                label = "Género",
                leadingIcon = Icons.Default.Transgender
            ) {
                gender = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                enableCreatePet = false
                val newPet = Pet(
                    name = name,
                    type = type,
                    gender = gender
                )
                // Llama al método de ViewModel para agregar la mascota
                petViewModel.addPet(newPet,
                    onSuccess = {
                        // Aquí puedes mostrar un mensaje o navegar después de crear la mascota
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("${newPet.name} registrado con éxito")
                            navigateBack()
                        }

                    },
                    onFailure = { exception ->
                        // Aquí puedes mostrar un error si algo sale mal
                        coroutineScope.launch {
                            enableCreatePet = true
                            snackBarHostState.showSnackbar("No se ha podido crear")
                            Log.d("Crear pet","Error: ${exception.message}" )
                        }
                    }
                )
            },
                enabled = enableCreatePet
            ) {
                Text(text = "Crear mascota")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(onClickIcon: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Nueva mascota",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onClickIcon()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = {

            }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray)
    )
}

@Composable
fun MyNavigationAppBar() {
    var index by remember { mutableIntStateOf(1) }
    NavigationBar(containerColor = Color.Red) {

        NavigationBarItem(
            selected = index == 0,
            onClick = { index = 0 },
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favourite icon",
                    tint = if (index == 0) Color.White else Color.LightGray
                )
            },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,


                )
        )

        NavigationBarItem(
            selected = index == 1,
            onClick = { index = 1 },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home icon",
                    tint = if (index == 1) Color.White else Color.LightGray
                )
            },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent

            )
        )

        NavigationBarItem(
            selected = index == 2,
            onClick = { index = 2 },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = if (index == 2) Color.White else Color.LightGray
                )
            },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent

            )
        )

    }
}

@Composable
fun MyFloatingActionButton() {
    FloatingActionButton(
        onClick = {

        },
        containerColor = Color.White,
        contentColor = Color.Red
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon")
    }
}


