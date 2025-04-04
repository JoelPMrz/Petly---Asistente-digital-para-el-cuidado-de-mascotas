package com.example.petly.ui.screens.login.pet

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petly.data.models.PetModel
import com.example.petly.navegation.Home
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.RealtimeManager
import kotlinx.coroutines.launch

@Composable
fun CreatePetScreen(analytics: AnalyticsManager,
                    auth: AuthManager,
                    realtime: RealtimeManager,
                    navigation : NavController
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var name: String by remember { mutableStateOf("") }
    var type: String by remember { mutableStateOf("") }
    var gender: String by remember { mutableStateOf("") }
    var weight: String by remember { mutableStateOf("") }
    val ownerId = auth.getCurrentUser()?.uid
    var newPet : PetModel
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MyTopAppBar(
                {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Has pulsado $it")
                    }
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
            BaseOutlinedTextField(
                value = weight,
                placeHolder = "8.2",
                label = "Peso",
                leadingIcon = Icons.Default.AutoMode
            ) {
                weight = it
            }
            Button(onClick = {
                newPet = PetModel(
                    name = name,
                    type = type,
                    gender = gender,
                    weight = null,
                    ownerId = ownerId.toString(),
                    breed = null,
                    birthDate = null,
                    photo = null,
                    microchipId = null,
                    adoptionDate = null
                )
                realtime.addPet(newPet)
                name = ""
                type = ""
                gender = ""
                weight = ""
                Toast.makeText(context,"${newPet.name} es tu mascota", Toast.LENGTH_SHORT).show()
                navigation.navigate(Home)
            }) {
                Text(text = "Crear mascota")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(onClickIcon: (String) -> Unit) {
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
                onClickIcon("Atrás")
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = {
                onClickIcon("Menú desplegado")
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


