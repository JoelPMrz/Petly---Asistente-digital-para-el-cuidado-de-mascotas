package com.example.petly.ui.screens.logged.weight

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Weight
import com.example.petly.ui.components.BaseDatePicker
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.BaseFAB
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.EmptyCard
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.convertWeight
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.parseDate
import com.example.petly.utils.truncate
import com.example.petly.viewmodel.PreferencesViewModel
import com.example.petly.viewmodel.WeightViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs

@Composable
fun WeightsScreen(
    //analytics: AnalyticsManager,
    petId: String,
    navigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    //val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val petState by petViewModel.petState.collectAsState()
    val weights by weightViewModel.weightsState.collectAsState()
    var showAddWeightDialog by remember { mutableStateOf(false) }
    val petName = petState?.name
    var selectedItemId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(petId) {
        petViewModel.getObservedPet(petId)
        weightViewModel.getWeights(petId)
    }

    Scaffold(
        topBar = {
            WeightsTopAppBar(
                navigateBack
            )
        },
        floatingActionButton = {
            BaseFAB(
                onClick = {
                    petState?.id?.let {
                        petViewModel.doesPetExist(
                            petId = it,
                            exists = { showAddWeightDialog = !showAddWeightDialog },
                            notExists = { Toast.makeText(context, "La mascota no existe", Toast.LENGTH_SHORT).show() },
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
                if (weights.isEmpty()) {
                    EmptyCard(
                        onClick = {
                            petState?.id?.let {
                                petViewModel.doesPetExist(
                                    petId = it,
                                    exists = { showAddWeightDialog = !showAddWeightDialog },
                                    notExists = { Toast.makeText(context, "La mascota no existe", Toast.LENGTH_SHORT).show() },
                                    onFailure = {}
                                )
                            }
                        },
                        text = stringResource(R.string.register_new_weight)
                    )
                }
            }

            items(weights.reversed(), key = { it.id!! }) { weight ->
                Weight(
                    weight = weight,
                    weights = weights,
                    petId = petId,
                    petName = petName,
                    selectedItemId = selectedItemId,
                    onSelectItem = { id ->
                        selectedItemId = id
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (showAddWeightDialog) {
            AddWeightBottomSheet(
                onDismiss = {
                    showAddWeightDialog = false
                },
                petId = petId
            )
        }
    }
}



@Composable
fun Weight(
    weight: Weight,
    weights: List<Weight>,
    petId: String,
    petName: String?,
    selectedItemId: String?,
    onSelectItem: (String?) -> Unit,
    weightViewModel: WeightViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val isExpanded = selectedItemId == weight.id
    var showEditWeightDialog by remember { mutableStateOf(false) }
    var showDeleteWeightDialog by remember { mutableStateOf(false) }
    val selectedUnit = preferencesViewModel.selectedUnit.collectAsState().value
    val convertedWeight = convertWeight(weight.value, weight.unit, selectedUnit).truncate(2)
    val difference: Double? = weightViewModel.comparePreviousWeight(weight, weights, selectedUnit)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if(isExpanded) onSelectItem(null) else onSelectItem(weight.id)
                    },
                    onLongPress = {
                        showDeleteWeightDialog = !showDeleteWeightDialog
                        onSelectItem(null)
                    }
                )
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
                Text(text = "$convertedWeight $selectedUnit")

                AnimatedVisibility(difference != null) {
                    difference?.let {
                        Row {
                            Icon(
                                imageVector = when {
                                    it < 0.0 -> Icons.Rounded.ArrowDropDown
                                    it == 0.0 -> Icons.AutoMirrored.Rounded.ArrowRight
                                    else -> Icons.Rounded.ArrowDropUp
                                },
                                contentDescription = "Arrow difference weight",
                                tint = when {
                                    it < 0.0 -> Color(0xFFFF6161)
                                    it == 0.0 -> Color(0xFF6879FF)
                                    else -> Color(0xFF58E561)
                                }
                            )
                            Text(text = "${abs(it)} $selectedUnit", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            AnimatedVisibility(visible = isExpanded) {
                Box {
                    if (!weight.notes.isNullOrEmpty()) {
                        Text(
                            text = weight.notes.toString(),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Justify,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier.fillMaxWidth().animateContentSize()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopStart),
                    text = formatLocalDateToString(weight.date),
                    fontSize = 10.sp
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    AnimatedVisibility(visible = isExpanded) {
                        IconCircle(
                            icon = Icons.Rounded.Delete,
                            onClick = {
                                showDeleteWeightDialog = !showDeleteWeightDialog
                                onSelectItem(null)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AnimatedVisibility(visible = isExpanded) {
                        IconCircle(
                            icon = Icons.Rounded.Edit,
                            onClick = {
                                showEditWeightDialog = !showEditWeightDialog
                                onSelectItem(null)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditWeightDialog) {
        AddWeightBottomSheet(
            onDismiss = {
                showEditWeightDialog = false
            },
            petId = petId,
            weight = weight
        )
    }

    if (showDeleteWeightDialog) {
        DeleteWeightDialog(
            onDismiss = {
                showDeleteWeightDialog = false
            },
            petId = petId,
            petName = petName,
            weight = weight,
        )
    }
}

@Composable
fun DeleteWeightDialog(
    onDismiss: () -> Unit,
    petId: String,
    weight: Weight,
    petName: String?,
    weightViewModel: WeightViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_weight_alert_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_weight_alert_description,
                    petName.toString()
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    weightViewModel.deleteWeight(
                        petId,
                        weight.id.toString(),
                        notPermission = {
                            Toast.makeText(context, "Permiso denegado para observadores", Toast.LENGTH_LONG).show()
                        }
                    )
                    onDismiss()
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
fun WeightsTopAppBar(
    navigateBack: () -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    var showUnitDialog by remember { mutableStateOf(false) }
    val selectedUnit = preferencesViewModel.selectedUnit.collectAsState().value
    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        title = {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text  = stringResource(R.string.wheights_pets_title),
                fontSize = 20.sp,
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
            Text(text = selectedUnit)
            Spacer(Modifier.width(10.dp))
            IconCircle(
                modifier = Modifier.size(35.dp),
                icon = ImageVector.vectorResource(id = R.drawable.ic_weight_24dp),
                onClick = {
                    showUnitDialog = true
                }
            )

            if (showUnitDialog) {
                SelectWeightUnitDialog(
                    onDismiss = {
                        showUnitDialog = false
                    }
                )
            }
        },
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWeightBottomSheet(
    onDismiss: () -> Unit,
    petId: String,
    weight: Weight? = null,
    weightViewModel: WeightViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedUnit = preferencesViewModel.selectedUnit.collectAsState().value
    var weightText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val noteMaxLength = 100
    var showUnitDialog by remember { mutableStateOf(false) }

    val openDatePicker = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var dateText by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(weight) {
        weight?.let {
            weightText = it.value.toString()
            note = it.notes.orEmpty()
            selectedDate.value = it.date
            dateText = formatLocalDateToString(it.date)
        } ?: run {
            selectedDate.value = LocalDate.now()
            dateText = formatLocalDateToString(LocalDate.now())
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
            },
            maxDate = LocalDate.now()
        )
    }

    if (showUnitDialog) {
        SelectWeightUnitDialog(
            onDismiss = { showUnitDialog = false }
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
                    if (weight != null) R.string.edit_weight_title
                    else R.string.create_weight_title
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
                value = weightText,
                modifier = Modifier.fillMaxWidth(),
                placeHolder = stringResource(R.string.weight_form_placeholder_weight),
                label = stringResource(R.string.weight_form_label_weight, selectedUnit),
                trailingIcon = ImageVector.vectorResource(id = R.drawable.ic_weight_24dp),
                onClickTrailingIcon = {
                showUnitDialog = true
                },
                maxLines = 1,
                isRequired = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            ){
                weightText = it
            }

            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = dateText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker.value = true },
                placeHolder = stringResource(R.string.weight_form_placeholder_date),
                label = stringResource(R.string.weight_form_label_date),
                trailingIcon = Icons.Outlined.CalendarToday,
                maxLines = 2,
                readOnly = true,
                onClickTrailingIcon = {
                    openDatePicker.value = true
                },
                isRequired = true
            ){

            }

            Spacer(modifier = Modifier.height(10.dp))

            BaseOutlinedTextField(
                value = note,
                modifier = Modifier.fillMaxWidth(),
                placeHolder = stringResource(R.string.weight_form_placeholder_note),
                label = stringResource(R.string.weight_form_label_note),
                maxLines = 3
            ){
                if (it.length <= noteMaxLength) note = it
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val parsedWeight = weightText.toDoubleOrNull()
                    val parsedDate = parseDate(dateText)
                    if (parsedWeight != null) {
                        val newWeight = Weight(
                            id = weight?.id,
                            petId = petId,
                            value = parsedWeight.truncate(2),
                            unit = selectedUnit,
                            date = parsedDate,
                            time = LocalDateTime.of(parsedDate, LocalTime.now()),
                            notes = note
                        )
                        if (weight != null) {
                            weightViewModel.updateWeight(
                                newWeight,
                                notPermission = {
                                    Toast.makeText(context, context.getString(R.string.permission_denied_observer), Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            weightViewModel.addWeight(
                                petId,
                                newWeight,
                                notPermission = {
                                    Toast.makeText(context, context.getString(R.string.permission_denied_observer), Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = stringResource(R.string.form_confirm_btn))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Composable
fun SelectWeightUnitDialog(
    onDismiss: () -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val weightUnits = listOf("Kg", "Gr", "Oz", "Lb")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.select_weght_unit_title_dialog))
        },
        text = {
            Column {
                weightUnits.forEach { unit ->
                    Text(
                        text = unit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                preferencesViewModel.setSelectedUnit(unit)
                                onDismiss()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.form_cancel_btn))
            }
        }
    )
}
