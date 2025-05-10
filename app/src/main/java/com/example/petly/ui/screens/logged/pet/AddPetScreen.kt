package com.example.petly.ui.screens.logged.pet

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
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
import java.time.LocalDate
import com.example.petly.ui.components.PhotoPickerBottomSheet
import com.example.petly.utils.TypeDropdownSelector
import com.example.petly.utils.isMicrochipIdValidOrEmpty

@Composable
fun AddPetScreen(
    analytics: AnalyticsManager,
    navigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }
    var name: String by remember { mutableStateOf("") }
    var incompleteName by remember { mutableStateOf(false) }
    var gender: String by remember { mutableStateOf("Male") }
    var type: String by remember { mutableStateOf("") }
    var incompleteType by remember { mutableStateOf(false) }
    var breed: String by remember { mutableStateOf("") }

    val openBirthDatePicker = remember { mutableStateOf(false) }
    val selectedBirthDate = remember { mutableStateOf<LocalDate?>(null) }
    var birthDateText by remember { mutableStateOf("") }

    val openAdoptionDatePicker = remember { mutableStateOf(false) }
    val selectedAdoptionDate = remember { mutableStateOf<LocalDate?>(null) }
    var adoptionDateText by remember { mutableStateOf("") }

    var microchipId by remember { mutableStateOf("") }
    var invalidMicrochipId by remember { mutableStateOf(false) }
    var sterilized by remember { mutableStateOf(false) }

    val newPet = Pet(
        name = name,
        type = type,
        gender = gender,
        breed = breed,
        birthDate = birthDateText.takeIf{ it.isNotBlank()}?.let { parseDate(it) },
        adoptionDate = adoptionDateText.takeIf { it.isNotBlank() }?.let { parseDate(it) },
        microchipId = microchipId,
        sterilized = sterilized
    )

    if (openBirthDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedBirthDate.value ?: LocalDate.now(),
            title = stringResource(R.string.birthdayDate),
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
            initialDate = selectedAdoptionDate.value ?: LocalDate.now(),
            title = stringResource(R.string.adoptionDate),
            onDismissRequest = { openAdoptionDatePicker.value = false },
            onDateSelected = { date ->
                selectedAdoptionDate.value = date
                adoptionDateText = formatLocalDateToString(date)
                openAdoptionDatePicker.value = false
            }
        )
    }

    if(showPhotoPicker){
        PhotoPickerBottomSheet(
            onImageSelected = { uri ->
                capturedImageUri = uri
            },
            onDismiss = {
                showPhotoPicker = false
            }
        )
    }

    Scaffold(
        bottomBar = {
            AddPetAppBar(
                newPet = newPet,
                petViewModel = petViewModel,
                navigateBack = navigateBack,
                capturedImageUri = capturedImageUri,
                incompleteName = {
                    incompleteName = it
                },
                incompleteType = {
                    incompleteType = it
                },
                invalidMicrochipId = {
                    invalidMicrochipId = it
                }
            )
        },
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
                    .height(220.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (capturedImageUri != Uri.EMPTY) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = capturedImageUri
                        ),
                        contentDescription = stringResource(R.string.profile_pet_photo_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                IconCircle(
                    icon = Icons.Rounded.ArrowBack,
                    onClick = navigateBack,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .align(Alignment.TopStart)
                )

                IconCircle(
                    icon = Icons.Rounded.CameraAlt,
                    onClick = { showPhotoPicker = true },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(if (capturedImageUri == Uri.EMPTY) 135.dp else 35.dp)
                        .align(if (capturedImageUri == Uri.EMPTY) Alignment.Center else Alignment.TopEnd),
                    sizeIcon = if (capturedImageUri == Uri.EMPTY) 80.dp else 24.dp
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp)
            ) {
                BaseOutlinedTextField(
                    value = name,
                    label = stringResource(R.string.name),
                    maxLines = 1,
                    maxLength = 25,
                    isError = incompleteName,
                    isRequired = true,
                ) { name = it }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    IconSquare(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .alpha(
                                if (gender == "Male") 1.0f else 0.3f
                            ),
                        icon = Icons.Rounded.Male,
                        onClick = { gender = "Male" },
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    IconSquare(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .alpha(
                                if (gender == "Transgender") 1.0f else 0.3f
                            ),
                        icon = Icons.Rounded.Transgender,
                        onClick = { gender = "Transgender" },
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    IconSquare(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .alpha(
                                if (gender == "Female") 1.0f else 0.3f
                            ),
                        icon = Icons.Rounded.Female,
                        onClick = { gender = "Female" },
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                TypeDropdownSelector(
                    type = type,
                    incompleteType = incompleteType,
                    onTypeSelected = { type = it },
                )

                BaseOutlinedTextField(
                    value = breed,
                    placeHolder = stringResource(R.string.carlino),
                    label = stringResource(R.string.breed),
                    maxLength = 22,
                    maxLines = 1
                ) { breed = it }

                Spacer(modifier = Modifier.height(5.dp))

                BaseOutlinedTextField(
                    value = birthDateText,
                    label = stringResource(R.string.birthdayDate),
                    trailingIcon = Icons.Rounded.CalendarMonth,
                    onClickTrailingIcon = { openBirthDatePicker.value = true },
                    maxLines = 1
                ) { birthDateText = it }

                Spacer(modifier = Modifier.height(5.dp))

                BaseOutlinedTextField(
                    value = adoptionDateText,
                    label = stringResource(R.string.adoptionDate),
                    trailingIcon = Icons.Rounded.CalendarMonth,
                    onClickTrailingIcon = { openAdoptionDatePicker.value = true },
                    maxLines = 1
                ) { adoptionDateText = it }

                Spacer(modifier = Modifier.height(5.dp))
                Column(Modifier.fillMaxWidth()){
                    BaseOutlinedTextField(
                        value = microchipId,
                        placeHolder = "941000023456789",
                        label = stringResource(R.string.microchip),
                        isError = invalidMicrochipId,
                        maxLines = 1,
                    ) { microchipId = it }

                    AnimatedVisibility(
                        visible = invalidMicrochipId
                    ) {
                        Text(stringResource(R.string.invalid_microchip), color = MaterialTheme.colorScheme.error)
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12))
                        .background(
                            if (sterilized) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer
                        )
                        .clickable { sterilized = !sterilized }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = if (sterilized) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                        contentDescription = null,
                        tint = if (sterilized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (sterilized) stringResource(R.string.sterilized) else stringResource(R.string.not_sterilized),
                        color = if (sterilized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Composable
fun AddPetAppBar(
    newPet: Pet,
    petViewModel: PetViewModel,
    incompleteName:(Boolean) -> Unit,
    incompleteType:(Boolean) -> Unit,
    invalidMicrochipId: (Boolean) -> Unit,
    navigateBack: () -> Unit,
    capturedImageUri: Uri,
) {
    var enableCreatePet by remember { mutableStateOf(true) }
    val context = LocalContext.current

    NavigationBar(
        containerColor = Color.Transparent
    ) {
        Button(
            onClick = {
                if (newPet.name.isBlank() || newPet.type.isBlank() || !isMicrochipIdValidOrEmpty(newPet.microchipId)) {
                    if (newPet.name.isBlank()) incompleteName(true) else incompleteName(false)
                    if (newPet.type.isBlank()) incompleteType(true) else incompleteType(false)
                    if(!isMicrochipIdValidOrEmpty(newPet.microchipId))invalidMicrochipId(true) else invalidMicrochipId(false)
                    return@Button
                } else {
                    invalidMicrochipId(false)
                    incompleteName(false)
                    incompleteType(false)
                    enableCreatePet = false
                    petViewModel.addPet(
                        pet = newPet,
                        onSuccess = {
                            Toast.makeText(context, "Bienvenido ${newPet.name}", Toast.LENGTH_SHORT).show()
                            if (capturedImageUri != Uri.EMPTY) {
                                newPet.id?.let {
                                    petViewModel.updatePetProfilePhoto(
                                        petId = it,
                                        newPhotoUri = capturedImageUri,
                                        notPermission = {

                                        },
                                        onSuccess = {
                                        },
                                        onFailure = { e ->
                                            Toast.makeText(context, "Imagen de ${newPet.name} no registrada", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            }
                            navigateBack()
                        },
                        onFailure = { e ->
                            enableCreatePet = true
                            Toast.makeText(context, "No se pudo registrar la mascota", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
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






