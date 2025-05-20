package com.example.petly.ui.screens.logged.pet

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.User
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.BaseFAB
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.EmptyCard
import com.example.petly.ui.components.BaseDatePicker
import com.example.petly.ui.components.BaseTimePicker
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AuthManager
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.formatLocalTimeToString
import com.example.petly.utils.parseDate
import com.example.petly.utils.parseTime
import com.example.petly.viewmodel.UserViewModel
import com.example.petly.viewmodel.VeterinaryVisitsViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun VeterinaryVisitsScreen(
    //analytics: AnalyticsManager,
    auth: AuthManager,
    petId: String,
    navigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    veterinaryVisitsViewModel: VeterinaryVisitsViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val petState by petViewModel.petState.collectAsState()
    val currentUserState by userViewModel.userState.collectAsState()
    val veterinaryVisits by veterinaryVisitsViewModel.veterinaryVisits.collectAsState()
    var showAddEditVeterinaryVisit by remember { mutableStateOf(false) }
    var itemSelected by remember { mutableStateOf<VeterinaryVisit?>(null) }

    LaunchedEffect(petId) {
        petViewModel.getObservedPet(petId)
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
                                    "La mascota no existe",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 30.dp)
        ) {
            item {
                if (veterinaryVisits.isEmpty()) {
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
                                            "La mascota no existe",
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

            items(veterinaryVisits, key = { it.id }) { veterinaryVisit ->
                VeterinaryVisitCard(
                    veterinaryVisit = veterinaryVisit,
                    petId = petId,
                    petName = petState?.name,
                    onClick = {
                        showAddEditVeterinaryVisit = true
                        itemSelected = veterinaryVisit
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (showAddEditVeterinaryVisit) {
            AddVeterinaryVisitBottomSheet(
                onDismiss = {
                    showAddEditVeterinaryVisit = false
                },
                petId = petId,
                petName = petState?.name ?: "",
                currentUser = currentUserState,
                veterinaryVisit = itemSelected
            )
        }
    }
}

@Composable
fun VeterinaryVisitCard(
    veterinaryVisit: VeterinaryVisit,
    petId: String,
    petName: String?,
    onClick: () -> Unit,
    veterinaryVisitsViewModel: VeterinaryVisitsViewModel = hiltViewModel()
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large
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
                Text(text = veterinaryVisit.concept)
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (!veterinaryVisit.description.isNullOrEmpty()) {
                Text(
                    text = veterinaryVisit.description.toString(),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Justify,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopStart),
                    text = formatLocalDateToString(veterinaryVisit.date),
                    fontSize = 10.sp
                )
                Text(
                    modifier = Modifier.align(Alignment.TopStart),
                    text = formatLocalTimeToString(veterinaryVisit.time),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DeleteVeterinaryVisitDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    petId: String,
    veterinaryVisit: VeterinaryVisit,
    petName: String?,
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
                    petName.toString()
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    veterinaryVisitsViewModel.deleteVeterinaryVisit(
                        petId,
                        veterinaryVisit.id,
                        notPermission = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.permission_denied_observer),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
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
) {

    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        title = {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.veterinary_visits),
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

        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVeterinaryVisitBottomSheet(
    onDismiss: () -> Unit,
    petId: String,
    petName: String,
    currentUser: User?,
    veterinaryVisit: VeterinaryVisit? = null,
    petViewModel: PetViewModel = hiltViewModel(),
    veterinaryVisitsViewModel: VeterinaryVisitsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var concept by remember { mutableStateOf("") }
    var isConceptError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var isDescriptionError by remember { mutableStateOf(true) }
    var veterinary by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }

    val openDatePicker = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var dateText by remember { mutableStateOf("") }

    val openTimePicker = remember { mutableStateOf(false) }
    val selectedTime = remember { mutableStateOf(LocalTime.now()) }
    var timeText by remember { mutableStateOf("") }

    var showDeleteVeterinaryVisit by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(petId) {
        petViewModel.getObservedPet(petId)
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
            onDismissRequest = { openDatePicker.value = false },
            onDateSelected = { date ->
                selectedDate.value = date
                dateText = formatLocalDateToString(date)
                openDatePicker.value = false
            }
        )
    }

    if (openTimePicker.value) {
        BaseTimePicker(
            initialTime = selectedTime.value,
            onDismissRequest = { openTimePicker.value = false },
            onTimeSelected = { time ->
                selectedTime.value = time
                timeText = formatLocalTimeToString(time)
                openTimePicker.value = false
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
                .padding(horizontal = 16.dp)
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
                modifier = Modifier.fillMaxWidth(),
                placeHolder = stringResource(R.string.concept_placeholder),
                label = stringResource(R.string.concept),
                maxLines = 2,
                maxLength = 35,
                isRequired = true,
                isError = isConceptError
            ) {
                concept = it
            }

            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = description,
                modifier = Modifier.fillMaxWidth(),
                placeHolder = stringResource(R.string.description_placeholder),
                label = stringResource(R.string.description),
                maxLines = 2,
                maxLength = 130,
                isError = isDescriptionError
            ) {
                description = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = veterinary,
                modifier = Modifier.fillMaxWidth(),
                placeHolder = stringResource(R.string.veterinary_placeholder),
                label = stringResource(R.string.veterinary),
                maxLines = 2,
                maxLength = 25
            ) {
                veterinary = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = dateText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker.value = true },
                placeHolder = dateText,
                label = stringResource(R.string.weight_form_label_date),
                trailingIcon = Icons.Outlined.CalendarToday,
                maxLines = 2,
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
                    .clickable { openTimePicker.value = true },
                placeHolder = timeText,
                label = stringResource(R.string.form_label_time),
                trailingIcon = Icons.Outlined.Schedule,
                maxLines = 2,
                readOnly = true,
                onClickTrailingIcon = {
                    openTimePicker.value = true
                },
                isRequired = true
            ) {

            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12))
                    .background(
                        if (completed) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                    .clickable {
                        completed = !completed
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = if (completed) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                    contentDescription = null,
                    tint = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (completed) stringResource(R.string.completed) else stringResource(
                        R.string.not_completed
                    ),
                    color = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val parsedDate = parseDate(dateText)
                        val parseTime = parseTime(timeText)
                        val newVeterinaryVisit = VeterinaryVisit(
                            id = veterinaryVisit?.id ?: "",
                            petId = petId,
                            concept = concept,
                            description = description,
                            date = parsedDate,
                            time = parseTime,
                            veterinary = veterinary,
                            createdBy = currentUser?.id ?: context.getString(R.string.unidentified),
                            completed = completed
                        )
                        if (veterinaryVisit != null) {
                            veterinaryVisitsViewModel.updateVeterinaryVisit(
                                veterinaryVisit = newVeterinaryVisit,
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
                        } else {
                            veterinaryVisitsViewModel.addVeterinaryVisit(
                                petId = petId,
                                veterinaryVisit = newVeterinaryVisit,
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
                        onDismiss()

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
                petId = petId,
                petName = petName,
                veterinaryVisit = it,
            )
        }
    }
}





