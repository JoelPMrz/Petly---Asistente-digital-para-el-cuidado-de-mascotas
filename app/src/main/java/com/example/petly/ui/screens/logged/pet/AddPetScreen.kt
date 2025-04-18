package com.example.petly.ui.screens.logged.pet

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.ui.components.BaseDatePicker
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.createImageFile
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.parseDate
import com.example.petly.utils.toTimestamp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Objects

@Composable
fun AddPetScreen(
    analytics: AnalyticsManager,
    navigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.example.petly" + ".provider", file
    )
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            Toast.makeText(context, "Permiso autorizado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if(it){
            Toast.makeText(context, "Foto realizada", Toast.LENGTH_SHORT).show()
            capturedImageUri = uri
            capturedImageUri?.let { uri ->
                scope.launch {

                }
            }
        }else{
            Toast.makeText(context, "La foto no se ha podido realizar", Toast.LENGTH_SHORT).show()
        }
    }




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
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
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
                        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if(permissionCheckResult == PackageManager.PERMISSION_GRANTED){

                        }else{
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
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

                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconSquare(
                        modifier = Modifier
                            .weight(1f)
                            .alpha(0.3f),
                        icon = Icons.Rounded.Female,
                        onClick = {

                        },
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
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

@Composable
fun AddPetAppBar(
    newPet: Pet,
    petViewModel: PetViewModel,
    snackBarHostState: SnackbarHostState,
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var enableCreatePet: Boolean by remember { mutableStateOf(true) }

    NavigationBar(
        containerColor = Color.Transparent
    ) {
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



