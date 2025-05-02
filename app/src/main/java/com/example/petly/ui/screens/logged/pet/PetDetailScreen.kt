package com.example.petly.ui.screens.logged.pet


import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.data.models.Weight
import com.example.petly.ui.components.BaseDatePicker
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.PetNotExistsDialog
import com.example.petly.ui.components.PhotoPickerBottomSheet
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.isValidMicrochipId
import com.example.petly.viewmodel.WeightViewModel
import java.time.LocalDate
import androidx.compose.animation.expandVertically as expandVertically1

@Composable
fun PetDetailScreen(
    analytics: AnalyticsManager,
    petId: String,
    navigateBack: () -> Unit,
    navigateToWeights: (String) -> Unit,
    navigateToHome: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var expandedMenu by remember { mutableStateOf(false) }
    var showEditMicrochip by remember { mutableStateOf(false) }
    var showPetNotExistsDialog by remember { mutableStateOf(false) }
    var showDeletePetDialog by remember { mutableStateOf(false) }
    var showEditSterilizedState by remember { mutableStateOf(false) }
    val petState by petViewModel.petState.collectAsState()
    val weights by weightViewModel.weightsState.collectAsState()
    var weight by remember { mutableStateOf<Weight?>(null) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }


    LaunchedEffect(petId) {
        petViewModel.getPetById(petId)
        weightViewModel.getWeights(petId)
    }
    LaunchedEffect(weights) {
        weight = weights.lastOrNull()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(20)
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                if (capturedImageUri != Uri.EMPTY) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = capturedImageUri
                        ),
                        contentDescription = stringResource(R.string.profile_pet_photo_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .clickable {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        showPhotoPicker = true
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = petState?.photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.pet_predeterminado),
                        error = painterResource(R.drawable.pet_predeterminado),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .clickable {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        showPhotoPicker = true
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            }
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
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconCircle(
                        modifier = Modifier
                            .size(35.dp),
                        icon = Icons.Rounded.MoreVert,
                        onClick = {
                            petViewModel.doesPetExist(
                                petId = petId,
                                exists = {
                                    expandedMenu = true
                                },
                                notExists = {
                                    showPetNotExistsDialog = true
                                },
                                onFailure = {
                                    //Analytics
                                }
                            )
                        },
                    )
                    Spacer(Modifier.height(5.dp))

                    DropdownPetMenu(
                        expanded = expandedMenu,
                        onDismiss = {
                            expandedMenu = false
                        },
                        onEdit = {
                            //llevar a la vista de edit
                            expandedMenu = false
                        },
                        onDelete = {
                            showDeletePetDialog = true
                            expandedMenu = false
                        }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-20).dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(30.dp)
                    )
                    .clip(RoundedCornerShape(30.dp))
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = petState?.name ?: "",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconCircle(
                        icon = when (petState?.gender) {
                            "Male" -> Icons.Rounded.Male
                            "Female" -> Icons.Rounded.Female
                            else -> Icons.Rounded.Transgender
                        },
                        modifier = Modifier.size(30.dp),
                        sizeIcon = 24.dp,
                        backgroundColor = when (petState?.gender) {
                            "Male" -> MaterialTheme.colorScheme.primaryContainer
                            "Female" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        },
                        contentColor = when (petState?.gender) {
                            "Male" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "Female" -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = petState?.type ?: "",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!petState?.breed.isNullOrEmpty()) {
                        Text(
                            text = (" - ${petState?.breed}"),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                    ) {
                        BirthdayCard(
                            pet = petState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        //Dialogo editar cumpleaños
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            }
                        )
                        Spacer(Modifier.width(10.dp))
                        AdoptionCard(
                            pet = petState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        //Dialogo editar Adopcion
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                    ) {
                        SterilizedCard(
                            pet = petState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        showEditSterilizedState = true
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            }
                        )
                        Spacer(Modifier.width(10.dp))
                        MicrochipCard(
                            pet = petState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        showEditMicrochip = true
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    WeigthCard(
                        weight,
                        petId,
                        Modifier.fillMaxWidth(),
                        onClick = {
                            petViewModel.doesPetExist(
                                petId = petId,
                                exists = {
                                    navigateToWeights(petId)
                                },
                                notExists = {
                                    showPetNotExistsDialog = true
                                },
                                onFailure = {
                                    //Analytics
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showPhotoPicker) {
        PhotoPickerBottomSheet(
            onImageSelected = { uri ->
                capturedImageUri = uri
                petViewModel.updatePetProfilePhoto(petId, capturedImageUri, onSuccess = {
                    Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Toast.makeText(context, "No se ha actualizado", Toast.LENGTH_SHORT).show()
                })
            },
            onDismiss = {
                showPhotoPicker = false
            }
        )
    }

    if (showPetNotExistsDialog) {
        PetNotExistsDialog(
            navigateTo = {
                navigateToHome()
            }
        )
    }

    if (showDeletePetDialog) {
        DeletePetDialog(
            context = context,
            onDismiss = { showDeletePetDialog = false },
            navigateToHome = { navigateToHome() },
            pet = petState
        )
    }

    if (showEditSterilizedState) {
        EditSterilizedStateBottomSheet(
            pet = petState,
            onDismiss = {
                showEditSterilizedState = false
            }
        )
    }

    if(showEditMicrochip){
        EditMicrochipBottomSheet(
            pet = petState,
            onDismiss = {
                showEditMicrochip = false
            }
        )
    }

}

@Composable
fun DropdownPetMenu(
    expanded: Boolean,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss() }
    ) {

        DropdownMenuItem(
            text = { Text("Editar") },
            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
            onClick = { onEdit() }
        )

        DropdownMenuItem(
            text = { Text("Eliminar") },
            leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null) },
            onClick = { onDelete() }
        )
    }
}

@Composable
fun DeletePetDialog(
    context: Context,
    onDismiss: () -> Unit,
    navigateToHome: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val petName = pet?.name ?: ""
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_pet_alert_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_pet_alert_description,
                    pet?.name ?: ""
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    pet?.id?.let {
                        petViewModel.deletePet(
                            it,
                            onSuccess = {
                                navigateToHome()
                                Toast.makeText(context, "$petName eliminado", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    "No se ha podido eliminar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            ) {
                Text(text = stringResource(R.string.form_confirm_delete_btn))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.form_cancel_btn))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSterilizedStateBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    var sterilized by remember { mutableStateOf(pet?.sterilized ?: false) }
    val openDatePicker = remember { mutableStateOf(false) }
    val selectedSterilizedDate = remember { mutableStateOf(pet?.sterilizedDate) }
    var sterilizedDateText by remember {
        mutableStateOf(pet?.sterilizedDate?.let {
            formatLocalDateToString(
                it
            )
        } ?: "")
    }
    var enableButton by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(start = 15.dp, end = 15.dp, bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Estado de esterilización")
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
                        .clickable {
                            sterilized = !sterilized
                            if (!sterilized) {
                                selectedSterilizedDate.value = null
                                sterilizedDateText = ""
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = if (sterilized) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                        contentDescription = null,
                        tint = if (sterilized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (sterilized) stringResource(R.string.sterilized) else stringResource(
                            R.string.not_sterilized
                        ),
                        color = if (sterilized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(
                    visible = sterilized,
                    enter = expandVertically1() + fadeIn(),
                    exit = shrinkVertically()
                ) {
                    BaseOutlinedTextField(
                        value = sterilizedDateText,
                        label = stringResource(R.string.sterilizedDate),
                        trailingIcon = Icons.Rounded.CalendarMonth,
                        onClickTrailingIcon = { openDatePicker.value = true },
                        maxLines = 1,
                        readOnly = true
                    ) {
                        sterilizedDateText = it
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            Button(
                onClick = {
                    pet?.id?.let {
                        petViewModel.updateSterilizedInfo(
                            petId = it,
                            sterilized = sterilized,
                            sterilizedDate = selectedSterilizedDate.value,
                            onSuccess = {
                                enableButton = false
                                onDismiss()
                            },
                            onFailure = {

                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .height(60.dp)
                    .align(Alignment.BottomCenter),
                enabled = enableButton
            ) {
                Text(text = "Guardar")
            }
        }
    }
    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedSterilizedDate.value ?: LocalDate.now(),
            onDismissRequest = { openDatePicker.value = false },
            onDateSelected = { date ->
                selectedSterilizedDate.value = date
                sterilizedDateText = formatLocalDateToString(date)
                openDatePicker.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMicrochipBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    var microchipId by remember { mutableStateOf(pet?.microchipId ?: "") }
    val openDatePicker = remember { mutableStateOf(false) }
    val selectedMicrochipDate = remember { mutableStateOf(pet?.microchipDate) }
    var microchipDateText by remember {
        mutableStateOf(pet?.microchipDate?.let {
            formatLocalDateToString(it)
        } ?: "")
    }
    var isValidated by remember { mutableStateOf(true) }  // Validación del microchip
    var enableButton by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(start = 15.dp, end = 15.dp, bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Datos del microchip")
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = microchipId,
                    placeHolder = "941000023456789",
                    label = stringResource(R.string.microchip_identifier),
                    maxLines = 1,
                    maxLength = 12,
                    isError = !isValidated
                ) { microchipId = it }

                AnimatedVisibility(
                    visible = !isValidated,
                    enter = expandVertically1() + fadeIn(),
                    exit = shrinkVertically()
                ) {
                    Text(stringResource(R.string.invalid_microchip), color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = isValidMicrochipId(microchipId),
                    enter = expandVertically1() + fadeIn(),
                    exit = shrinkVertically()
                ) {
                    BaseOutlinedTextField(
                        value = microchipDateText,
                        label = stringResource(R.string.microchipDate),
                        trailingIcon = Icons.Rounded.CalendarMonth,
                        onClickTrailingIcon = { openDatePicker.value = true },
                        maxLines = 1,
                        isError = false,
                        readOnly = true
                    ) { microchipDateText = it }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
            Button(
                onClick = {
                    if (isValidMicrochipId(microchipId)) {
                        pet?.id?.let {
                            petViewModel.updateMicrochipInfo(
                                petId = it,
                                microchipId = microchipId,
                                microchipDate = selectedMicrochipDate.value,
                                onSuccess = {
                                    enableButton = false
                                    onDismiss()
                                },
                                onFailure = {
                                    // Analytics
                                }
                            )
                        }
                    } else {
                        isValidated = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .height(60.dp)
                    .align(Alignment.BottomCenter),
                enabled = enableButton
            ) {
                Text(text = "Guardar")
            }
        }
    }

    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedMicrochipDate.value ?: LocalDate.now(),
            onDismissRequest = { openDatePicker.value = false },
            onDateSelected = { date ->
                selectedMicrochipDate.value = date
                microchipDateText = formatLocalDateToString(date)
                openDatePicker.value = false
            }
        )
    }
}




