package com.jdev.petly.ui.screens.logged.pet

import BaseTimePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jdev.petly.R
import com.jdev.petly.data.models.Pet
import com.jdev.petly.data.models.User
import com.jdev.petly.data.models.VeterinaryVisit
import com.jdev.petly.ui.components.BaseDatePicker
import com.jdev.petly.ui.components.BaseFAB
import com.jdev.petly.ui.components.BaseOutlinedTextField
import com.jdev.petly.ui.components.DividerWithText
import com.jdev.petly.ui.components.EmptyCard
import com.jdev.petly.ui.components.IconCircle
import com.jdev.petly.ui.components.pet.PetNotExistsDialog
import com.jdev.petly.ui.viewmodel.PetViewModel
import com.jdev.petly.utils.AuthManager
import com.jdev.petly.utils.formatLocalDateToString
import com.jdev.petly.utils.formatLocalDateToStringWithDay
import com.jdev.petly.utils.formatLocalTimeToString
import com.jdev.petly.utils.parseDate
import com.jdev.petly.utils.parseTime
import com.jdev.petly.viewmodel.PreferencesViewModel
import com.jdev.petly.viewmodel.UserViewModel
import com.jdev.petly.viewmodel.VeterinaryVisitsViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun VeterinaryVisitsScreen(
    //analytics: AnalyticsManager,
    auth: AuthManager,
    petId: String,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    veterinaryVisitsViewModel: VeterinaryVisitsViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val petState by petViewModel.petState.collectAsState()
    var showPetNotExistsDialog by remember { mutableStateOf(false) }
    val currentUserState by userViewModel.userState.collectAsState()
    val veterinaryVisits by veterinaryVisitsViewModel.veterinaryVisits.collectAsState()
    var showAddEditVeterinaryVisit by remember { mutableStateOf(false) }
    var showFilterVeterinaryVisits by remember { mutableStateOf(false) }
    var itemSelected by remember { mutableStateOf<VeterinaryVisit?>(null) }
    val selectedVisitFilter = preferencesViewModel.visitFilter.collectAsState().value

    val subTitle: String? = when (selectedVisitFilter) {
        "next" -> stringResource(R.string.next_visits_subtitle)
        "previous" -> stringResource(R.string.previous_visits_subtitle)
        "previous_not_attending" -> stringResource(R.string.previous_not_attending_visits_subtitle)
        else -> null
    }
    val filteredVisits = veterinaryVisits.filter { visit ->
        val dateTime = LocalDateTime.of(visit.date, visit.time)
        when (selectedVisitFilter) {
            "next" -> dateTime.isAfter(LocalDateTime.now())
            "previous" -> dateTime.isBefore(LocalDateTime.now())
            "previous_not_attending" -> !visit.completed && dateTime.isBefore(LocalDateTime.now())
            else -> true
        }
    }
    val sortedVisits = when (selectedVisitFilter) {
        "next" -> filteredVisits.sortedBy { LocalDateTime.of(it.date, it.time) }
        "previous", "previous_not_attending" -> filteredVisits.sortedByDescending { LocalDateTime.of(it.date, it.time) }
        "all" -> {
            val upcoming = filteredVisits.filter {
                LocalDateTime.of(it.date, it.time).isAfter(LocalDateTime.now())
            }.sortedBy { LocalDateTime.of(it.date, it.time) }

            val past = filteredVisits.filter {
                LocalDateTime.of(it.date, it.time).isBefore(LocalDateTime.now())
            }.sortedByDescending { LocalDateTime.of(it.date, it.time) }

            upcoming + past
        }
        else -> filteredVisits
    }

    LaunchedEffect(petId) {
        petViewModel.getObservedPet(
            petId,
            petNotExits = {
                showPetNotExistsDialog = true
            }
        )
        veterinaryVisitsViewModel.getVeterinaryVisitsFlow(petId)
    }

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

    Scaffold(
        topBar = {
            VeterinaryVisitsTopAppBar(
                navigateBack,
                onClickFilter = {
                    showFilterVeterinaryVisits = true
                }
            )
        },
        floatingActionButton = {
            BaseFAB(
                onClick = {
                    petState?.id?.let {
                        petViewModel.doesPetExist(
                            petId = it,
                            exists = { showAddEditVeterinaryVisit = !showAddEditVeterinaryVisit },
                            notExists = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.pet_not_exists_dialog_title),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = {}
                        )
                    }
                },
                imageVector = Icons.Rounded.Add
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            subTitle?.let {
                AnimatedVisibility(
                    visible = true,
                ) {
                    DividerWithText(it, modifier = Modifier.padding(horizontal = 20.dp))
                }
            }
            Spacer(Modifier.height(5.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                item {
                    if (filteredVisits.isEmpty()) {
                        EmptyCard(
                            onClick = {
                                petState?.id?.let {
                                    petViewModel.doesPetExist(
                                        petId = it,
                                        exists = {
                                            showAddEditVeterinaryVisit = !showAddEditVeterinaryVisit
                                        },
                                        notExists = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.pet_not_exists_dialog_title),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onFailure = {}
                                    )
                                }
                            },
                            text = stringResource(R.string.register_new_veterinay_visists)
                        )
                    }
                }
                if (selectedVisitFilter == "all") {
                    val upcomingVisits = sortedVisits.filter { LocalDateTime.of(it.date, it.time).isAfter(LocalDateTime.now()) }
                    val pastVisits = sortedVisits.filter { LocalDateTime.of(it.date, it.time).isBefore(LocalDateTime.now()) }

                    if (upcomingVisits.isNotEmpty()) {
                        item {
                            DividerWithText(stringResource(R.string.next_visits_subtitle), modifier = Modifier)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        items(upcomingVisits, key = { it.id }) { veterinaryVisit ->
                            VeterinaryVisitCard(
                                veterinaryVisit = veterinaryVisit,
                                onClick = {
                                    showAddEditVeterinaryVisit = true
                                    itemSelected = veterinaryVisit
                                },
                                pet = petState
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }else if (filteredVisits.isNotEmpty()){
                        item{
                            DividerWithText(stringResource(R.string.next_visits_subtitle), modifier = Modifier)
                            Spacer(modifier = Modifier.height(5.dp))
                            EmptyCard(
                                onClick = {
                                    petState?.id?.let {
                                        petViewModel.doesPetExist(
                                            petId = it,
                                            exists = {
                                                showAddEditVeterinaryVisit = !showAddEditVeterinaryVisit
                                            },
                                            notExists = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.pet_not_exists_dialog_title),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onFailure = {}
                                        )
                                    }
                                },
                                text = stringResource(R.string.register_new_veterinay_visists)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }

                    if (pastVisits.isNotEmpty()) {
                        item {
                            if (upcomingVisits.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            DividerWithText(stringResource(R.string.previous_visits_subtitle), modifier = Modifier)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        items(pastVisits, key = { it.id }) { veterinaryVisit ->
                            VeterinaryVisitCard(
                                veterinaryVisit = veterinaryVisit,
                                onClick = {
                                    showAddEditVeterinaryVisit = true
                                    itemSelected = veterinaryVisit
                                },
                                pet = petState
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                } else {
                    items(sortedVisits, key = { it.id }) { veterinaryVisit ->
                        VeterinaryVisitCard(
                            veterinaryVisit = veterinaryVisit,
                            onClick = {
                                showAddEditVeterinaryVisit = true
                                itemSelected = veterinaryVisit
                            },
                            pet = petState
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        if (showAddEditVeterinaryVisit) {
            petState?.let { pet ->
                AddVeterinaryVisitBottomSheet(
                    onDismiss = {
                        showAddEditVeterinaryVisit = false
                        itemSelected = null
                    },
                    pet = pet,
                    currentUser = currentUserState,
                    veterinaryVisit = itemSelected
                )
            }
        }
    }

    if (showFilterVeterinaryVisits) {
        VeterinaryVisitsFilterBottomSheet(
            onDismiss = {
                showFilterVeterinaryVisits = false
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
}

@Composable
fun VeterinaryVisitCard(
    veterinaryVisit: VeterinaryVisit,
    showImage: Boolean = false,
    onClick: () -> Unit,
    pet: Pet?,
) {
    val dateTime = LocalDateTime.of(veterinaryVisit.date, veterinaryVisit.time)
    val photo = pet?.photo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when {
                !veterinaryVisit.completed && dateTime.isBefore(LocalDateTime.now()) -> MaterialTheme.colorScheme.errorContainer
                veterinaryVisit.completed -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = veterinaryVisit.concept, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if(showImage){
                    if (photo != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photo)
                                .placeholder(R.drawable.pet_predeterminado)
                                .error(R.drawable.pet_predeterminado)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.pet_predeterminado),
                            contentDescription = "Pet profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (!veterinaryVisit.description.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = veterinaryVisit.description.toString(),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }


            if (!veterinaryVisit.veterinary.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(5.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCircle(
                            modifier = Modifier.size(18.dp),
                            sizeIcon = 18.dp,
                            icon = ImageVector.vectorResource(id = R.drawable.home_health_24dp),
                            backgroundColor = when {
                                !veterinaryVisit.completed && dateTime.isBefore(LocalDateTime.now()) -> MaterialTheme.colorScheme.onErrorContainer
                                veterinaryVisit.completed -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            contentColor = when {
                                !veterinaryVisit.completed && dateTime.isBefore(LocalDateTime.now()) -> MaterialTheme.colorScheme.errorContainer
                                veterinaryVisit.completed -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = veterinaryVisit.veterinary.toString(),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Justify,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formatLocalDateToStringWithDay(veterinaryVisit.date),
                    fontSize = 12.sp
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = formatLocalTimeToString(veterinaryVisit.time),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DeleteVeterinaryVisitDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    pet : Pet,
    veterinaryVisit: VeterinaryVisit,
    veterinaryVisitsViewModel: VeterinaryVisitsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_visit_alert_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_visit_alert_description,
                    pet.name
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    pet.id?.let {
                        veterinaryVisitsViewModel.deleteVeterinaryVisit(
                            it,
                            veterinaryVisit.id,
                            notPermission = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.permission_denied_observer),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                    onDismiss()
                    onDelete()
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
fun VeterinaryVisitsTopAppBar(
    navigateBack: () -> Unit,
    onClickFilter: () -> Unit
) {

    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        title = {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.veterinary_visits_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconCircle(
                modifier = Modifier.size(35.dp),
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = {
                    navigateBack()
                }
            )
        },
        actions = {
            IconCircle(
                modifier = Modifier.size(30.dp),
                icon = Icons.Rounded.FilterAlt,
                onClick = {
                    onClickFilter()
                }
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVeterinaryVisitBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet,
    currentUser: User?,
    veterinaryVisit: VeterinaryVisit? = null,
    petViewModel: PetViewModel = hiltViewModel(),
    veterinaryVisitsViewModel: VeterinaryVisitsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    var concept by remember { mutableStateOf("") }
    var isConceptError by remember { mutableStateOf(false) }
    val conceptFocusRequester = remember { FocusRequester() }
    var wasConceptTouched by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }
    val descriptionFocusRequester = remember { FocusRequester() }

    var veterinary by remember { mutableStateOf("") }
    val veterinaryFocusRequester = remember { FocusRequester() }

    var completed by remember { mutableStateOf(false) }

    val openDatePicker = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var dateText by remember { mutableStateOf("") }
    val dateFocusRequester = remember { FocusRequester() }

    val openTimePicker = remember { mutableStateOf(false) }
    val selectedTime = remember { mutableStateOf(LocalTime.now()) }
    var timeText by remember { mutableStateOf("") }
    val timeFocusRequester = remember { FocusRequester() }

    val isPastUncompletedVeterinaryVisit = veterinaryVisit != null &&
            !completed &&
            LocalDateTime.of(selectedDate.value, selectedTime.value).isBefore(LocalDateTime.now())

    var showDeleteVeterinaryVisit by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showPetNotExistsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pet.id) {
        pet.id?.let { petViewModel.getObservedPet(
            it,
            petNotExits = {
                showPetNotExistsDialog = true
            }
        ) }
    }

    LaunchedEffect(veterinaryVisit) {
        veterinaryVisit?.let { veterinaryVisit ->
            concept = veterinaryVisit.concept
            description = veterinaryVisit.description.orEmpty()
            selectedDate.value = veterinaryVisit.date
            selectedTime.value = veterinaryVisit.time
            dateText = formatLocalDateToString(veterinaryVisit.date)
            timeText = formatLocalTimeToString(veterinaryVisit.time)
            veterinary = veterinaryVisit.veterinary ?: ""
            completed = veterinaryVisit.completed
        } ?: run {
            selectedDate.value = LocalDate.now()
            dateText = formatLocalDateToString(LocalDate.now())
            selectedTime.value = LocalTime.now()
            timeText = formatLocalTimeToString(LocalTime.now())
        }
    }

    if (openDatePicker.value) {
        BaseDatePicker(
            initialDate = selectedDate.value,
            onDismissRequest = {
                openDatePicker.value = false
                timeFocusRequester.requestFocus()
            },
            onDateSelected = { date ->
                selectedDate.value = date
                dateText = formatLocalDateToString(date)
                openDatePicker.value = false
                timeFocusRequester.requestFocus()
            }
        )
    }

    if (openTimePicker.value) {
        BaseTimePicker(
            initialTime = selectedTime.value,
            onDismissRequest = {
                openTimePicker.value = false
                focusManager.clearFocus()
            },
            onTimeSelected = { time ->
                selectedTime.value = time
                timeText = formatLocalTimeToString(time)
                openTimePicker.value = false
                focusManager.clearFocus()
            }
        )
    }

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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    if (veterinaryVisit != null) R.string.edit_veterinay_vivist_title
                    else R.string.create_veterinay_vivist_title
                ),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp
            )

            BaseOutlinedTextField(
                value = concept,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(conceptFocusRequester)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            if (wasConceptTouched) {
                                isConceptError = concept.isBlank()
                            }
                        } else {
                            wasConceptTouched = true
                        }
                    },
                placeHolder = stringResource(R.string.concept_placeholder),
                label = stringResource(R.string.concept),
                maxLines = 2,
                maxLength = 35,
                isRequired = true,
                isError = isConceptError,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        descriptionFocusRequester.requestFocus()
                    }
                )
            ) {
                concept = it
                if (wasConceptTouched && it.isNotBlank()) {
                    isConceptError = false
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = description,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocusRequester),
                placeHolder = stringResource(R.string.description_placeholder),
                label = stringResource(R.string.description),
                maxLines = 2,
                maxLength = 100,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        veterinaryFocusRequester.requestFocus()
                    }
                )
            ) {
                description = it
            }

            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = veterinary,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(veterinaryFocusRequester),
                placeHolder = stringResource(R.string.veterinary_placeholder),
                label = stringResource(R.string.veterinary),
                maxLines = 2,
                maxLength = 25,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        dateFocusRequester.requestFocus()
                    }
                )
            ) {
                veterinary = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = dateText,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(dateFocusRequester)
                    .clickable { openDatePicker.value = true },
                placeHolder = dateText,
                label = stringResource(R.string.weight_form_label_date),
                trailingIcon = ImageVector.vectorResource(id = R.drawable.calendar_today_24dp),
                maxLines = 1,
                readOnly = true,
                onClickTrailingIcon = {
                    openDatePicker.value = true
                },
                isRequired = true
            ) {

            }

            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = timeText,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(timeFocusRequester)
                    .clickable { openTimePicker.value = true },
                placeHolder = timeText,
                label = stringResource(R.string.form_label_time),
                trailingIcon = ImageVector.vectorResource(id = R.drawable.calendar_clock_24dp),
                maxLines = 1,
                readOnly = true,
                onClickTrailingIcon = {
                    openTimePicker.value = true
                },
                isRequired = true
            ) {

            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12))
                    .background(
                        when {
                            isPastUncompletedVeterinaryVisit -> MaterialTheme.colorScheme.onErrorContainer
                            completed -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .clickable {
                        completed = !completed
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = when {
                        isPastUncompletedVeterinaryVisit -> Icons.Rounded.EventBusy
                        completed -> Icons.Rounded.EventAvailable
                        else -> ImageVector.vectorResource(id = R.drawable.event_upcoming_24dp)
                    },
                    contentDescription = null,
                    tint = when {
                        isPastUncompletedVeterinaryVisit -> MaterialTheme.colorScheme.errorContainer
                        completed -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isPastUncompletedVeterinaryVisit -> stringResource(R.string.has_not_been_completed)
                        completed -> stringResource(R.string.completed)
                        else -> stringResource(R.string.not_completed)
                    },
                    color = when {
                        isPastUncompletedVeterinaryVisit -> MaterialTheme.colorScheme.errorContainer
                        completed -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (concept.isBlank()) {
                            isConceptError = true
                            conceptFocusRequester.requestFocus()
                            return@Button
                        } else {
                            val parsedDate = parseDate(dateText)
                            val parseTime = parseTime(timeText)
                            val newVeterinaryVisit = pet.id?.let { petId ->
                                VeterinaryVisit(
                                    id = veterinaryVisit?.id ?: "",
                                    petId = petId,
                                    concept = concept,
                                    description = description,
                                    date = parsedDate,
                                    time = parseTime,
                                    veterinary = veterinary,
                                    createdBy = currentUser?.id
                                        ?: context.getString(R.string.unidentified),
                                    completed = completed
                                )
                            }
                            if (veterinaryVisit != null) {
                                newVeterinaryVisit?.let { veterinaryVisit ->
                                    veterinaryVisitsViewModel.updateVeterinaryVisit(
                                        veterinaryVisit = veterinaryVisit,
                                        veterinaryVisitNotExist = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.veterinaryVisit_not_exist),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        notPermission = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.permission_denied_observer),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.error_edit_veterinary_visit),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                }
                            } else {
                                pet.id?.let {
                                    newVeterinaryVisit?.let { veterinaryVisit ->
                                        veterinaryVisitsViewModel.addVeterinaryVisit(
                                            petId = it,
                                            veterinaryVisit = veterinaryVisit,
                                            notPermission = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.permission_denied_observer),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            },
                                            onFailure = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.error_create_veterinary_visit),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                ) {
                    Text(
                        text = if (veterinaryVisit == null) stringResource(R.string.add) else stringResource(
                            R.string.edit
                        )
                    )
                }

                if (veterinaryVisit != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            showDeleteVeterinaryVisit = true
                        },
                        modifier = Modifier
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(text = stringResource(R.string.delete))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDeleteVeterinaryVisit) {
        veterinaryVisit?.let {
            DeleteVeterinaryVisitDialog(
                onDismiss = {
                    showDeleteVeterinaryVisit = false
                },
                onDelete = {
                    onDismiss()
                },
                pet = pet,
                veterinaryVisit = it,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinaryVisitsFilterBottomSheet(
    onDismiss: () -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {

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
                text = stringResource(R.string.filter_veterinary_visits),
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        enableButton = false
                        preferencesViewModel.setVisitFilter("next")
                        onDismiss()
                    }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCircle(
                        icon = ImageVector.vectorResource(id = R.drawable.event_upcoming_24dp),
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.next_vivits),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        enableButton = false
                        preferencesViewModel.setVisitFilter("previous")
                        onDismiss()
                    }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconCircle(
                        icon = Icons.Outlined.EventAvailable,
                        backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.previous_visits),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                }
            }
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        enableButton = false
                        preferencesViewModel.setVisitFilter("previous_not_attending")
                        onDismiss()
                    }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconCircle(
                        icon = Icons.Outlined.EventBusy,
                        backgroundColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.previous_not_attending_visits),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )

                }
            }
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        enableButton = false
                        preferencesViewModel.setVisitFilter("all")
                        onDismiss()
                    }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconCircle(
                        icon = Icons.Outlined.CalendarMonth,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.all_visits),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}






