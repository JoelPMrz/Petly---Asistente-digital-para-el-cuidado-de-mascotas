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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.models.Weight
import com.example.petly.ui.components.BaseDatePicker
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.components.pet.PetNotExistsDialog
import com.example.petly.ui.components.PhotoPickerBottomSheet
import com.example.petly.ui.components.pet.AdoptionCard
import com.example.petly.ui.components.pet.BirthdayCard
import com.example.petly.ui.components.pet.MicrochipCard
import com.example.petly.ui.components.pet.PeopleLinkedCard
import com.example.petly.ui.components.pet.SterilizedCard
import com.example.petly.ui.components.pet.VeterinaryVisitsCard
import com.example.petly.ui.components.pet.WeigthCard
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.TypeDropdownSelector
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.getAgeFromDate
import com.example.petly.utils.isMicrochipIdValid
import com.example.petly.viewmodel.UserViewModel
import com.example.petly.viewmodel.WeightViewModel
import java.time.LocalDate
import androidx.compose.animation.expandVertically as expandVertically1

@Composable
fun PetDetailScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    petId: String,
    navigateBack: () -> Unit,
    navigateToOwners: (String) -> Unit,
    navigateToObservers: (String) -> Unit,
    navigateToWeights: (String) -> Unit,
    navigateToVeterinaryVisits: (String) -> Unit,
    navigateToHome: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var expandedMenu by remember { mutableStateOf(false) }
    var showEditBasicData by remember { mutableStateOf(false) }
    var showEditBirthDate by remember { mutableStateOf(false) }
    var showEditAdoptionDate by remember { mutableStateOf(false) }
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
        petViewModel.getObservedPet(petId)
        weightViewModel.getWeights(petId)
    }

    LaunchedEffect(weights) {
        weight = weights.lastOrNull()
    }

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

    petState?.id?.let {
        petViewModel.doesPetExist(
            petId = it,
            exists = {},
            notExists = { showPetNotExistsDialog = true },
            onFailure = {}
        )
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
                            showEditBasicData = true
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
                    .offset(y = (-25).dp)
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
                        modifier = Modifier.weight(1f),
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
                                        showEditBirthDate = true
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
                                        showEditAdoptionDate = true
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                    ) {
                        PeopleLinkedCard(
                            pet = petState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        navigateToOwners(petId)
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            },
                            role = "owners"
                        )
                        Spacer(Modifier.width(10.dp))
                        PeopleLinkedCard(
                            pet = petState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                petViewModel.doesPetExist(
                                    petId = petId,
                                    exists = {
                                        navigateToObservers(petId)
                                    },
                                    notExists = {
                                        showPetNotExistsDialog = true
                                    },
                                    onFailure = {
                                        //Analytics
                                    }
                                )
                            },
                            role = "observers"
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
                    VeterinaryVisitsCard(
                        petId = petId,
                        modifier = Modifier,
                        onClick = {
                            navigateToVeterinaryVisits(petId)
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
                petViewModel.updatePetProfilePhoto(
                    petId = petId,
                    newPhotoUri = capturedImageUri,
                    notPermission = {
                        Toast.makeText(context, context.getString(R.string.permission_denied_observer) , Toast.LENGTH_LONG).show()
                    },
                    onSuccess = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.updated_photo),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFailure = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.not_updated_photo),
                            Toast.LENGTH_SHORT
                        ).show()
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

    if (showEditBasicData) {
        EditBasicDataBottomSheet(
            pet = petState,
            onDismiss = {
                showEditBasicData = false
            }
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

    if (showEditBirthDate) {
        EditBirthDateBottomSheet(
            pet = petState,
            onDismiss = {
                showEditBirthDate = false
            }
        )
    }

    if (showEditAdoptionDate) {
        EditAdoptionDateBottomSheet(
            pet = petState,
            onDismiss = {
                showEditAdoptionDate = false
            }
        )
    }

    if (showEditMicrochip) {
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
            text = { Text(stringResource(R.string.edit)) },
            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
            onClick = { onEdit() }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.delete)) },
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
                            notPermission = {
                                Toast.makeText(
                                    context,
                                    "Permiso denegado para observadores",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            onSuccess = {
                                navigateToHome()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.deleted_pet, petName),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.not_deleted_pet),
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
fun EditBasicDataBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(pet?.name ?: "") }
    var incompleteName by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(pet?.type ?: "") }
    var incompleteType by remember { mutableStateOf(false) }
    var breed by remember { mutableStateOf(pet?.breed ?: "") }
    var gender by remember { mutableStateOf(pet?.gender ?: "Male") }

    var enableButton by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.edit_basic_data_title),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
                1.dp
            )
            Spacer(modifier = Modifier.height(10.dp))
            BaseOutlinedTextField(
                value = name,
                label = stringResource(R.string.name),
                maxLines = 1,
                maxLength = 25,
                isError = incompleteName,
                isRequired = true
            ) {
                name = it
            }
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
            Spacer(modifier = Modifier.height(10.dp))
            TypeDropdownSelector(
                type = type,
                incompleteType = incompleteType,
                onTypeSelected = { type = it },
            )
            Spacer(modifier = Modifier.height(10.dp))
            BaseOutlinedTextField(
                value = breed,
                placeHolder = stringResource(R.string.carlino),
                label = stringResource(R.string.breed),
                maxLength = 22,
                maxLines = 1
            ) { breed = it }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    pet?.id?.let {
                        if (name.isBlank() || type.isBlank()) {
                            incompleteName = name.isBlank()
                            incompleteType = type.isBlank()
                            return@Button
                        } else {
                            petViewModel.updateBasicData(
                                petId = it,
                                name = name,
                                type = type,
                                breed = breed,
                                gender = gender,
                                notPermission = {
                                    Toast.makeText(
                                        context,
                                        "Permiso denegado para observadores",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                onSuccess = {
                                    enableButton = false
                                    onDismiss()
                                },
                                onFailure = {
                                    //Analytics
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = enableButton
            ) {
                Text(
                    text = stringResource(R.string.edit)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBirthDateBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val openDatePicker = remember { mutableStateOf(false) }
    val selectedBirthdate = remember { mutableStateOf(pet?.birthDate) }
    val age = getAgeFromDate(selectedBirthdate.value)
    var birthdateText by remember {
        mutableStateOf(pet?.birthDate?.let {
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
                Text(
                    text = stringResource(R.string.edit_birthdate_title),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    1.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = birthdateText,
                    label = stringResource(R.string.birthdayDate),
                    trailingIcon = Icons.Rounded.CalendarMonth,
                    onClickTrailingIcon = { openDatePicker.value = true },
                    maxLines = 1,
                    readOnly = true
                ) {
                    birthdateText = it
                }
                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = birthdateText.isNotBlank(),
                    enter = expandVertically1() + fadeIn(),
                    exit = shrinkVertically()
                ) {
                    BaseOutlinedTextField(
                        value = age ?: "",
                        label = stringResource(R.string.age),
                        maxLines = 1,
                        readOnly = true
                    ) { }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            Button(
                onClick = {
                    pet?.id?.let {
                        petViewModel.updateBirthdate(
                            petId = it,
                            birthDate = selectedBirthdate.value,
                            notPermission = {
                                Toast.makeText(
                                    context,
                                    "Permiso denegado para observadores",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
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
                Text(
                    text = if (pet?.birthDate == null) stringResource(R.string.save) else stringResource(
                        R.string.edit
                    )
                )
            }
        }
    }
    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedBirthdate.value ?: LocalDate.now(),
            title = stringResource(R.string.birthdayDate),
            maxDate = LocalDate.now(),
            onDismissRequest = { openDatePicker.value = false },
            onDateSelected = { date ->
                selectedBirthdate.value = date
                birthdateText = formatLocalDateToString(date)
                openDatePicker.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAdoptionDateBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val openDatePicker = remember { mutableStateOf(false) }
    val selectedAdoptionDate = remember { mutableStateOf(pet?.adoptionDate) }
    val time = getAgeFromDate(selectedAdoptionDate.value)
    var adoptionDateText by remember {
        mutableStateOf(pet?.adoptionDate?.let {
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
                Text(
                    text = stringResource(R.string.adoptionDate),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    1.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = adoptionDateText,
                    label = stringResource(R.string.adoptionDate),
                    trailingIcon = Icons.Rounded.CalendarMonth,
                    onClickTrailingIcon = { openDatePicker.value = true },
                    maxLines = 1,
                    readOnly = true
                ) {
                    adoptionDateText = it
                }
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(
                    visible = adoptionDateText.isNotBlank(),
                    enter = expandVertically1() + fadeIn(),
                    exit = shrinkVertically()
                ) {
                    BaseOutlinedTextField(
                        value = time ?: "",
                        label = stringResource(R.string.time),
                        maxLines = 1,
                        readOnly = true
                    ) { }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            Button(
                onClick = {
                    pet?.id?.let {
                        petViewModel.updateAdoptionDate(
                            petId = it,
                            adoptionDate = selectedAdoptionDate.value,
                            notPermission = {
                                Toast.makeText(
                                    context,
                                    "Permiso denegado para observadores",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
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
                Text(
                    text = if (pet?.adoptionDate == null) stringResource(R.string.save) else stringResource(
                        R.string.edit
                    )
                )
            }
        }
    }
    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedAdoptionDate.value ?: LocalDate.now(),
            title = stringResource(R.string.adoptionDate),
            maxDate = LocalDate.now(),
            onDismissRequest = { openDatePicker.value = false },
            onDateSelected = { date ->
                selectedAdoptionDate.value = date
                adoptionDateText = formatLocalDateToString(date)
                openDatePicker.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSterilizedStateBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
                Text(
                    text = stringResource(R.string.edit_sterilized_state_title),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    1.dp
                )
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
                            notPermission = {
                                Toast.makeText(
                                    context,
                                    "Permiso denegado para observadores",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
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
                Text(text = stringResource(R.string.save))
            }
        }
    }
    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedSterilizedDate.value ?: LocalDate.now(),
            title = stringResource(R.string.sterilizedDate),
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
    val context = LocalContext.current
    var microchipId by remember { mutableStateOf(pet?.microchipId ?: "") }
    val openDatePicker = remember { mutableStateOf(false) }
    val selectedMicrochipDate = remember { mutableStateOf(pet?.microchipDate) }
    var microchipDateText by remember {
        mutableStateOf(pet?.microchipDate?.let {
            formatLocalDateToString(it)
        } ?: "")
    }
    var isValidated by remember { mutableStateOf(true) }
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
                Text(
                    text = stringResource(R.string.edit_microchip_title),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    1.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(Modifier.fillMaxWidth()) {
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
                        Text(
                            stringResource(R.string.invalid_microchip),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(
                    visible = isMicrochipIdValid(microchipId),
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
                    if (isMicrochipIdValid(microchipId)) {
                        pet?.id?.let {
                            petViewModel.updateMicrochipInfo(
                                petId = it,
                                microchipId = microchipId,
                                microchipDate = selectedMicrochipDate.value,
                                notPermission = {
                                    Toast.makeText(
                                        context,
                                        "Permiso denegado para observadores",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
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
                Text(
                    text = if (pet?.microchipId.isNullOrBlank()) stringResource(R.string.save) else stringResource(
                        R.string.edit
                    )
                )
            }
        }
    }

    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedMicrochipDate.value ?: LocalDate.now(),
            title = stringResource(R.string.microchipDate),
            onDismissRequest = { openDatePicker.value = false },
            onDateSelected = { date ->
                selectedMicrochipDate.value = date
                microchipDateText = formatLocalDateToString(date)
                openDatePicker.value = false
            }
        )
    }
}




