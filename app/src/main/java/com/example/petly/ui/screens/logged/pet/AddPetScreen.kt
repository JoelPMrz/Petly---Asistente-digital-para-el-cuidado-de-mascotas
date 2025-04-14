package com.example.petly.ui.screens.logged.pet

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.ui.components.BaseDatePicker
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.parseDate
import com.example.petly.utils.toTimestamp
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AddPetScreen(
    analytics: AnalyticsManager,
    navigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var name: String by remember { mutableStateOf("") }
    var gender: String by remember { mutableStateOf("Male") }
    var type: String by remember { mutableStateOf("") }
    var breed: String by remember { mutableStateOf("") }

    val openBirthDatePicker = remember { mutableStateOf(false) }
    val selectedBirthDate = remember { mutableStateOf(LocalDate.now()) }
    var birthDateText by remember { mutableStateOf("") }

    val openAdoptionDatePicker = remember { mutableStateOf(false) }
    val selectedAdoptionDate = remember { mutableStateOf(LocalDate.now()) }
    var adoptionDateText by remember { mutableStateOf("") }

    var microchipId by remember { mutableStateOf("") }

    var sterilized by remember { mutableStateOf(false) }

    val newPet = Pet(
        name = name,
        type = type,
        gender = gender,
        breed = breed,
        birthDate = (parseDate(birthDateText)),
        adoptionDate = parseDate(adoptionDateText),
        microchipId = microchipId
    )

    if (openBirthDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedBirthDate.value,
            onDismissRequest = { openBirthDatePicker.value = false },
            onDateSelected = { date ->
                selectedBirthDate.value = date
                birthDateText = formatLocalDateToString(date)
                openBirthDatePicker.value = false
            }
        )
    }

    if (openAdoptionDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedAdoptionDate.value,
            onDismissRequest = { openAdoptionDatePicker.value = false },
            onDateSelected = { date ->
                selectedAdoptionDate.value = date
                adoptionDateText = formatLocalDateToString(date)
                openAdoptionDatePicker.value = false
            }
        )
    }

    Scaffold(
        bottomBar = { AddPetAppBar(newPet, petViewModel, snackBarHostState, navigateBack) },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(200.dp)
                    .background(
                        color = Color.Blue,
                        shape = RoundedCornerShape(12, 12, 6, 6)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                IconCircle(
                    Icons.Rounded.ArrowBack,
                    onClick = {
                        navigateBack()
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .align(Alignment.TopStart)
                )

                IconCircle(
                    Icons.Rounded.CameraAlt,
                    onClick = {

                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .align(Alignment.TopEnd)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
            {

                BaseOutlinedTextField(
                    value = name,
                    label = "Nombre",
                    maxLines = 1
                ) {
                    name = it
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconSquare(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Male,
                        onClick = {

                        },
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconSquare(
                        modifier = Modifier
                            .weight(1f)
                            .alpha(0.3f),
                        icon = Icons.Rounded.Female,
                        onClick = {

                        }
                    )

                }
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = type,
                    placeHolder = "Perro",
                    label = "Espécie",
                    maxLines = 1
                ) {
                    type = it
                }
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = breed,
                    placeHolder = "Carlino",
                    label = "Raza",
                    maxLines = 1
                ) {
                    breed = it
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row{
                    BaseOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = birthDateText,
                        label = "Nacimiento",
                        trailingIcon = Icons.Rounded.CalendarMonth,
                        onClickTrailingIcon = {
                            openBirthDatePicker.value = true
                        },
                        maxLines = 1
                    ) {
                        birthDateText = it
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    BaseOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = adoptionDateText,
                        label = "Adopción",
                        trailingIcon = Icons.Rounded.CalendarMonth,
                        onClickTrailingIcon = {
                            openAdoptionDatePicker.value = true
                        },
                        maxLines = 1
                    ) {
                        adoptionDateText = it
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = microchipId,
                    placeHolder = "224774HTSD675",
                    label = "MicroChip",
                    maxLines = 1
                ) {
                    microchipId = it
                }

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
                "Registrar mascota",
                fontSize = 20.sp,
                fontWeight = FontWeight.W400,
                fontStyle = FontStyle.Italic,
                color = Color.DarkGray
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onClickIcon()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun AddPetAppBar(
    newPet: Pet,
    petViewModel: PetViewModel,
    snackBarHostState: SnackbarHostState,
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var enableCreatePet: Boolean by remember { mutableStateOf(true) }

    NavigationBar {
        Button(
            onClick = {
                petViewModel.addPet(
                    newPet,
                    onSuccess = {
                        enableCreatePet = false
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("${newPet.name} registrado con éxito")
                            navigateBack()
                        }

                    },
                    onFailure = { exception ->
                        coroutineScope.launch {
                            enableCreatePet = true
                            snackBarHostState.showSnackbar("No se ha podido crear")
                            Log.d("Crear pet", "Error: ${exception.message}")
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(60.dp),
            enabled = enableCreatePet
        ) {
            Text(
                text = "Registrar",
                fontSize = 18.sp
            )
        }

    }
}

@Composable
fun MyNavigationAppBar() {
    var index by remember { mutableIntStateOf(1) }
    NavigationBar {

        NavigationBarItem(
            selected = index == 0,
            onClick = { index = 0 },
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
            onClick = { index = 1 },
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
            onClick = { index = 2 },
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



