package com.example.petly.ui.screens.logged.pet


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PersonAddAlt
import androidx.compose.material.icons.rounded.PersonAddAlt1
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.data.models.Weight
import com.example.petly.data.models.getAge
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.components.PhotoPickerBottomSheet
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.convertWeight
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.truncate
import com.example.petly.viewmodel.PreferencesViewModel
import com.example.petly.viewmodel.WeightViewModel

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
    var showDeletePetDialog by remember { mutableStateOf(false) }
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
                                showPhotoPicker = true
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
                                showPhotoPicker = true
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
                        onClick = { expandedMenu = true },
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BirthdayCard(
                            pet = petState,
                            modifier = Modifier.weight(1f),
                            onClick = { }
                        )
                        Spacer(Modifier.width(10.dp))
                        AdoptionCard(
                            pet = petState,
                            modifier = Modifier.weight(1f),
                            onClick = { }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SterilizedCard(
                            pet = petState,
                            modifier = Modifier.weight(1f),
                            onClick = { }
                        )
                        Spacer(Modifier.width(10.dp))
                        MicrochipCard(
                            pet = petState,
                            modifier = Modifier.weight(1f),
                            onClick = { }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Spacer(Modifier.height(10.dp))
                    Weigths(weight, petId, Modifier.fillMaxWidth(), navigateToWeights)
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

    if(showDeletePetDialog){
        DeletePetDialog(
            context = context,
            onDismiss = {showDeletePetDialog = false},
            navigateToHome = {navigateToHome()},
            pet = petState
        )
    }

}

@Composable
fun BirthdayCard(
    pet: Pet?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = Icons.Outlined.Cake,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Edad", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = pet?.getAge() ?: "Agregar edad",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = pet?.birthDate?.let { formatLocalDateToString(it) } ?: "Agregar fecha",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun SterilizedCard(
    pet: Pet?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = Icons.Outlined.HealthAndSafety,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Estado", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = if (pet?.sterilized == true) "Esterilizado" else "No esteriliazo",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = pet?.sterilizedDate?.let { formatLocalDateToString(it) } ?: "Agregar fecha",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun MicrochipCard(
    pet: Pet?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = Icons.Outlined.Memory,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Microchip", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = pet?.microchipId ?: "Agregar identificador",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = pet?.microchipDate?.let { formatLocalDateToString(it) } ?: "Agregar fecha",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AdoptionCard(
    pet: Pet?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = Icons.Outlined.VolunteerActivism,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Adopción", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = pet?.adoptionDate?.let { formatLocalDateToString(it) } ?: "Agregar fecha",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}


@Composable
fun Weigths(
    weight: Weight?,
    petId: String,
    modifier: Modifier,
    navigateToWeights: (String) -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        preferencesViewModel.reloadUnitPreference()
    }

    val selectedUnit by preferencesViewModel.selectedUnit.collectAsState()
    var convertedWeight by remember { mutableStateOf("Agregar un peso") }
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(weight, selectedUnit) {
        if (weight != null) {
            convertedWeight =
                convertWeight(weight.value, weight.unit, selectedUnit).truncate(2).toString()
            dateString = formatLocalDateToString(weight.date)
        } else {
            convertedWeight = "Agregar un peso"
            dateString = ""
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp)
            .clickable { navigateToWeights(petId) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = Icons.Outlined.MonitorWeight,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Peso actual", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = "$convertedWeight $selectedUnit",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = dateString,
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.End)
            )
        }
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
    context : Context,
    onDismiss: () -> Unit,
    navigateToHome:() -> Unit,
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
                                Toast.makeText(context, "$petName Eliminado", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "No se ha podido eliminar", Toast.LENGTH_SHORT).show()
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




