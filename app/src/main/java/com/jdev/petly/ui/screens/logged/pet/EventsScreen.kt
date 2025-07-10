package com.jdev.petly.ui.screens.logged.pet

import BaseTimePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Person
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
import com.jdev.petly.data.models.Event
import com.jdev.petly.data.models.Pet
import com.jdev.petly.data.models.User
import com.jdev.petly.ui.components.BaseDatePicker
import com.jdev.petly.ui.components.BaseFAB
import com.jdev.petly.ui.components.BaseOutlinedTextField
import com.jdev.petly.ui.components.DividerWithText
import com.jdev.petly.ui.components.EmptyCard
import com.jdev.petly.ui.components.IconCircle
import com.jdev.petly.ui.components.pet.PetNotExistsDialog
import com.jdev.petly.ui.viewmodel.PetViewModel
import com.jdev.petly.utils.AuthManager
import com.jdev.petly.utils.formatLocalDateTimeToString
import com.jdev.petly.utils.formatLocalDateToString
import com.jdev.petly.utils.formatLocalDateToStringWithDay
import com.jdev.petly.utils.formatLocalTimeToString
import com.jdev.petly.utils.parseDate
import com.jdev.petly.utils.parseTime
import com.jdev.petly.viewmodel.NormalEventViewModel
import com.jdev.petly.viewmodel.PreferencesViewModel
import com.jdev.petly.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun EventsScreen(
    //analytics: AnalyticsManager,
    auth: AuthManager,
    petId: String,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    normalEventViewModel: NormalEventViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val petState by petViewModel.petState.collectAsState()
    var showPetNotExistsDialog by remember { mutableStateOf(false) }
    val currentUserState by userViewModel.userState.collectAsState()
    val events by normalEventViewModel.events.collectAsState()
    var showAddEditEvents by remember { mutableStateOf(false) }
    var showFilterEvents by remember { mutableStateOf(false) }
    var itemSelected by remember { mutableStateOf<Event?>(null) }
    val selectedEventFilter = preferencesViewModel.filterEvents.collectAsState().value

    val subTitle: String? = when (selectedEventFilter) {
        "next" -> stringResource(R.string.next_events_subtitle)
        "previous" -> stringResource(R.string.previous_visits_subtitle)
        "previous_not_attending" -> stringResource(R.string.previous_not_attending_visits_subtitle)
        else -> null
    }
    val filteredEvents = events.filter { event ->
        val dateTime = LocalDateTime.of(event.date, event.time)
        when (selectedEventFilter) {
            "next" -> dateTime.isAfter(LocalDateTime.now())
            "previous" -> dateTime.isBefore(LocalDateTime.now())
            "previous_not_attending" -> !event.completed && dateTime.isBefore(LocalDateTime.now())
            else -> true
        }
    }
    val sortedEvents = when (selectedEventFilter) {
        "next" -> filteredEvents.sortedBy { LocalDateTime.of(it.date, it.time) }
        "previous", "previous_not_attending" -> filteredEvents.sortedByDescending {
            LocalDateTime.of(
                it.date,
                it.time
            )
        }

        "all" -> {
            val upcoming = filteredEvents.filter {
                LocalDateTime.of(it.date, it.time).isAfter(LocalDateTime.now())
            }.sortedBy { LocalDateTime.of(it.date, it.time) }

            val past = filteredEvents.filter {
                LocalDateTime.of(it.date, it.time).isBefore(LocalDateTime.now())
            }.sortedByDescending { LocalDateTime.of(it.date, it.time) }

            upcoming + past
        }

        else -> filteredEvents
    }

    LaunchedEffect(petId) {
        petViewModel.getObservedPet(
            petId,
            petNotExits = {
                showPetNotExistsDialog = true
            }
        )
        normalEventViewModel.getEventsFlow(petId)
    }

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

    Scaffold(
        topBar = {
            EventsTopAppBar(
                navigateBack,
                onClickFilter = {
                    showFilterEvents = true
                }
            )
        },
        floatingActionButton = {
            BaseFAB(
                onClick = {
                    petState?.id?.let {
                        petViewModel.doesPetExist(
                            petId = it,
                            exists = { showAddEditEvents = !showAddEditEvents },
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
                    DividerWithText(it, modifier = Modifier.padding(horizontal = 30.dp))
                }
            }
            Spacer(Modifier.height(5.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                item {
                    if (filteredEvents.isEmpty()) {
                        EmptyCard(
                            onClick = {
                                petState?.id?.let {
                                    petViewModel.doesPetExist(
                                        petId = it,
                                        exists = {
                                            showAddEditEvents = !showAddEditEvents
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
                            text = stringResource(R.string.register_new_event)
                        )
                    }
                }
                if (selectedEventFilter == "all") {
                    val upcomingEvents = sortedEvents.filter {
                        LocalDateTime.of(it.date, it.time).isAfter(LocalDateTime.now())
                    }
                    val pastEvents = sortedEvents.filter {
                        LocalDateTime.of(it.date, it.time).isBefore(LocalDateTime.now())
                    }

                    if (upcomingEvents.isNotEmpty()) {
                        item {
                            DividerWithText(
                                stringResource(R.string.next_events_subtitle),
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        items(upcomingEvents, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                onClick = {
                                    showAddEditEvents = true
                                    itemSelected = event
                                },
                                pet = petState
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    } else if (filteredEvents.isNotEmpty()) {
                        item {
                            DividerWithText(
                                stringResource(R.string.next_events_subtitle),
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            EmptyCard(
                                onClick = {
                                    petState?.id?.let {
                                        petViewModel.doesPetExist(
                                            petId = it,
                                            exists = {
                                                showAddEditEvents = !showAddEditEvents
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
                                text = stringResource(R.string.register_new_event)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }

                    if (pastEvents.isNotEmpty()) {
                        item {
                            if (upcomingEvents.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            DividerWithText(
                                stringResource(R.string.previous_visits_subtitle),
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        items(pastEvents, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                onClick = {
                                    showAddEditEvents = true
                                    itemSelected = event
                                },
                                pet = petState
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                } else {
                    items(sortedEvents, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            onClick = {
                                showAddEditEvents = true
                                itemSelected = event
                            },
                            pet = petState
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

        }

        if (showAddEditEvents) {
            petState?.let { pet ->
                AddEventBottomSheet(
                    onDismiss = {
                        showAddEditEvents = false
                        itemSelected = null
                    },
                    pet = pet,
                    currentUser = currentUserState,
                    event = itemSelected
                )
            }
        }
    }

    if (showFilterEvents) {
        EventsFilterBottomSheet(
            onDismiss = {
                showFilterEvents = false
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
fun EventCard(
    event: Event,
    showImage: Boolean = false,
    onClick: () -> Unit,
    pet: Pet?,
) {
    val dateTime = LocalDateTime.of(event.date, event.time)
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
                !event.completed && dateTime.isBefore(LocalDateTime.now()) -> MaterialTheme.colorScheme.errorContainer
                event.completed -> MaterialTheme.colorScheme.primaryContainer
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
                Text(
                    text = event.concept,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (showImage) {
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

            if (event.description.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.description,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formatLocalDateToStringWithDay(event.date),
                    fontSize = 12.sp
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = formatLocalTimeToString(event.time),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DeleteEventDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    pet: Pet,
    event: Event,
    normalEventViewModel: NormalEventViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_event_alert_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_event_alert_description,
                    pet.name
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    pet.id?.let { petId ->
                        normalEventViewModel.deleteEvent(
                            petId = petId,
                            eventId = event.id,
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
fun EventsTopAppBar(
    navigateBack: () -> Unit,
    onClickFilter: () -> Unit
) {

    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        title = {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.events_title),
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
fun AddEventBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet,
    currentUser: User?,
    event: Event? = null,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    normalEventViewModel: NormalEventViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var concept by remember { mutableStateOf("") }
    var isConceptError by remember { mutableStateOf(false) }
    val conceptFocusRequester = remember { FocusRequester() }
    var wasConceptTouched by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }
    val descriptionFocusRequester = remember { FocusRequester() }

    var completed by remember { mutableStateOf(false) }
    var createdBy by remember { mutableStateOf("") }

    val openDatePicker = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var dateText by remember { mutableStateOf("") }
    val dateFocusRequester = remember { FocusRequester() }

    val openTimePicker = remember { mutableStateOf(false) }
    val selectedTime = remember { mutableStateOf(LocalTime.now()) }
    var timeText by remember { mutableStateOf("") }
    val timeFocusRequester = remember { FocusRequester() }

    val isPastUncompletedEvent = event != null &&
            !completed &&
            LocalDateTime.of(selectedDate.value, selectedTime.value).isBefore(LocalDateTime.now())

    var showDeleteEvent by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPetNotExistsDialog by remember { mutableStateOf(false) }

    var showDetailsTracker by remember { mutableStateOf(false) }
    val eventCreatorUser by userViewModel.eventCreatorUserState.collectAsState()
    val eventEditorUser by userViewModel.eventEditorUserState.collectAsState()


    LaunchedEffect(pet.id) {
        pet.id?.let {
            petViewModel.getObservedPet(
                it,
                petNotExits = {
                    showPetNotExistsDialog = true
                }
            )
        }
    }

    LaunchedEffect(event) {
        event?.let { event ->
            concept = event.concept
            description = event.description
            selectedDate.value = event.date
            selectedTime.value = event.time
            dateText = formatLocalDateToString(event.date)
            timeText = formatLocalTimeToString(event.time)
            completed = event.completed
            createdBy = event.createdBy
        } ?: run {
            selectedDate.value = LocalDate.now()
            dateText = formatLocalDateToString(LocalDate.now())
            selectedTime.value = LocalTime.now()
            timeText = formatLocalTimeToString(LocalTime.now())
        }
    }

    LaunchedEffect(event?.createdBy) {
        event?.createdBy?.let { userViewModel.getEventCreatorUserFlowById(it) }
    }

    LaunchedEffect(event?.editedBy) {
        val editorId = event?.editedBy
        if (!editorId.isNullOrBlank()) {
            userViewModel.getEventEditorUserFlowById(editorId)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(
                        if (event != null) R.string.edit_event_title
                        else R.string.create_event_title
                    ),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (event != null){
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable {
                                showDetailsTracker = !showDetailsTracker
                            }
                            .align(Alignment.CenterEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_lupa_rastreadora),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp
            )

            AnimatedVisibility(showDetailsTracker) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp)
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.event_creator),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = eventCreatorUser?.name ?: stringResource(R.string.unidentified),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.event_editor),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (event?.editedBy.isNullOrBlank()) {
                            Text(
                                text = stringResource(R.string.event_not_edited),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        } else {
                            Text(
                                text = eventEditorUser?.name ?: stringResource(R.string.unidentified),
                                style = MaterialTheme.typography.labelMedium,
                            )

                            event?.lastEditAt?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatLocalDateTimeToString(it),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

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
                        dateFocusRequester.requestFocus()
                        openDatePicker.value = true
                    }
                )
            ) {
                description = it
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
                            isPastUncompletedEvent -> MaterialTheme.colorScheme.onErrorContainer
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
                        isPastUncompletedEvent -> Icons.Rounded.EventBusy
                        completed -> Icons.Rounded.EventAvailable
                        else -> ImageVector.vectorResource(id = R.drawable.event_upcoming_24dp)
                    },
                    contentDescription = null,
                    tint = when {
                        isPastUncompletedEvent -> MaterialTheme.colorScheme.errorContainer
                        completed -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isPastUncompletedEvent -> stringResource(R.string.has_not_been_completed)
                        completed -> stringResource(R.string.completed)
                        else -> stringResource(R.string.not_completed)
                    },
                    color = when {
                        isPastUncompletedEvent -> MaterialTheme.colorScheme.errorContainer
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
                            val newEvent = pet.id?.let { petId ->
                                Event(
                                    id = event?.id ?: "",
                                    petId = petId,
                                    concept = concept,
                                    description = description,
                                    date = parsedDate,
                                    time = parseTime,
                                    createdBy = createdBy,
                                    completed = completed
                                )
                            }
                            if (event != null) {
                                pet.id?.let {
                                    newEvent?.let { event ->
                                        event.editedBy = currentUser?.id
                                            ?: context.getString(R.string.unidentified)
                                        event.lastEditAt = LocalDateTime.now()
                                        normalEventViewModel.updateEvent(
                                            event = event,
                                            eventNotExist = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.event_not_exist),
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
                                                    context.getString(R.string.error_edit_event),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            } else {
                                pet.id?.let {
                                    newEvent?.let { event ->
                                        event.createdBy = currentUser?.id
                                            ?: context.getString(R.string.unidentified)
                                        normalEventViewModel.addEvent(
                                            petId = it,
                                            event = event,
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
                                                    context.getString(R.string.error_create_event),
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
                        text = if (event == null) stringResource(R.string.add) else stringResource(
                            R.string.edit
                        )
                    )
                }

                if (event != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            showDeleteEvent = true
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

    if (showDeleteEvent) {
        event?.let {
            DeleteEventDialog(
                onDismiss = {
                    showDeleteEvent = false
                },
                onDelete = {
                    onDismiss()
                },
                pet = pet,
                event = it,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsFilterBottomSheet(
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
                text = stringResource(R.string.filter_events),
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
                        preferencesViewModel.setEventsFilter("next")
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
                        stringResource(R.string.next_events),
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
                        preferencesViewModel.setEventsFilter("previous")
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
                        stringResource(R.string.previous_events),
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
                        preferencesViewModel.setEventsFilter("previous_not_attending")
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
                        stringResource(R.string.previous_not_attending_events),
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
                        preferencesViewModel.setEventsFilter("all")
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
                        stringResource(R.string.all_events),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}




