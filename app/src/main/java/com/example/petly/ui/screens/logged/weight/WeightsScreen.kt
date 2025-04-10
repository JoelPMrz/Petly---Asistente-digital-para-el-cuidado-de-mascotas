package com.example.petly.ui.screens.logged.weight

import android.R.attr.visible
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Weight
import com.example.petly.data.models.WeightUnit
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.viewmodel.WeightViewModel


@Composable
fun WeightsScreen(
    analytics: AnalyticsManager,
    petId: String,
    navigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val petState by petViewModel.petState.collectAsState()
    val weights by weightViewModel.weightsState.collectAsState()

    LaunchedEffect(petId) {
        petViewModel.getPetById(petId)
        weightViewModel.getWeights(petId)
    }


    Scaffold(
        topBar = {
            WeightsTopAppBar(
                navigateBack,
                petId,
                petState?.name ?: "Nombre no disponible",
                weightViewModel
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {
                if (weights.isEmpty()) {
                    Text(text = "No tienes pesos")
                } else {
                    LazyColumn(modifier = Modifier.padding(10.dp)) {
                        items(weights.reversed()) { weight ->
                            Weight(weight, weights, weightViewModel, petId)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Weight(weight: Weight, weights: List<Weight>, weightViewModel: WeightViewModel, petId: String) {
    val difference: Double? = weightViewModel.comparePreviousWeight(weight, weights)
    var expanded by remember { mutableStateOf(false) }
    var showEditWeightDialog by remember { mutableStateOf(false) }
    var showDeleteWeightDialog by remember { mutableStateOf(false)  }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        expanded = !expanded
                    },
                    onLongPress = {
                        //weightViewModel.deleteWeight(petId = petId, weightId = weight.id.toString())
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
                Text(text = "${weight.value} kilos")
                Row {
                    if (difference != null) {
                        Icon(
                            imageVector = when {
                                difference < 0.0 -> Icons.Rounded.ArrowDropDown
                                difference == 0.0 -> Icons.Rounded.ArrowRight
                                else -> Icons.Rounded.ArrowDropUp
                            },
                            contentDescription = "Arrow difference weight",
                            tint = when {
                                difference < 0.0 -> Color.Red
                                difference == 0.0 -> Color.Blue
                                else -> Color.Green
                            }
                        )
                        Text(text = "${kotlin.math.abs(difference)}")
                    }
                }
            }
            AnimatedVisibility(expanded) {
                Box() {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = weight.notes ?: "No hay notas disponibles",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "21 Marzo 2025",
                    fontSize = 10.sp
                )
                AnimatedVisibility(expanded) {
                    Row {
                        IconCircle(
                            Icons.Rounded.Delete,
                            onClick = {
                                showDeleteWeightDialog = !showDeleteWeightDialog
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconCircle(
                            Icons.Rounded.Edit,
                            onClick = {
                                showEditWeightDialog = !showEditWeightDialog
                            }
                        )
                    }
                }
            }
        }
    }
    if (showEditWeightDialog) {
        AddWeightDialog(
            onDismiss = {
                showEditWeightDialog = false
            },
            petId = petId,
            weightViewModel = weightViewModel,
            weight = weight
        )
    }
    if(showDeleteWeightDialog){
        DeleteAlertDialog(
            onDismiss = {
                expanded = false
            },
            petId = petId,
            weightViewModel = weightViewModel,
            weight = weight
        )
    }

}

@Composable
fun DeleteAlertDialog(
    onDismiss: () -> Unit,
    weightViewModel: WeightViewModel,
    petId: String,
    weight: Weight,
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_weight_alert_title))
        },
        text = {
            Text(text = stringResource(R.string.delete_weight_alert_description))
        },
        confirmButton = {
            weightViewModel.deleteWeight(petId, weight.id.toString())
        },
        dismissButton = {
            onDismiss()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightsTopAppBar(
    navigateBack: () -> Unit,
    petId: String,
    petName: String,
    weightViewModel: WeightViewModel
) {
    var showUnitDialog by remember { mutableStateOf(false) }
    var showAddWeightDialog by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            Text(
                "Pesos de $petName",
                fontSize = 20.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navigateBack()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Arrowback icon")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    showAddWeightDialog = true
                }
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add weight")
            }
            //Text(text= selectedUnit)
            IconButton(onClick = {
                showUnitDialog = true
            }) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "Scale icon")
            }
            if (showUnitDialog) {
                SelectWeightUnitDialog(
                    onUnitSelected = { unit ->
                        selectedUnit = unit.toString()
                        println("Unidad seleccionada: $unit")
                    },
                    onDismiss = {
                        showUnitDialog = false
                    }
                )
            }
            if (showAddWeightDialog) {
                AddWeightDialog(
                    onDismiss = {
                        showAddWeightDialog = false
                    },
                    petId = petId,
                    weightViewModel = weightViewModel
                )
            }
        },
        //colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun AddWeightDialog(
    onDismiss: () -> Unit,
    petId: String,
    weightViewModel: WeightViewModel,
    weight: Weight? = null
) {
    var weightText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    val noteMaxLength = 100

    LaunchedEffect(key1 = weight) {
        weight?.let {
            weightText = it.value.toString()
            note = it.notes ?: ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (weight != null) {
                    stringResource(R.string.edit_weight_title)
                } else {
                    stringResource(R.string.create_weight_title)
                }

            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.weight_form_placeholder_weight))
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.weight_form_label_weight),
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.MonitorWeight,
                            contentDescription = Icons.Outlined.MonitorWeight.name,
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.weight_form_placeholder_date))
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.weight_form_label_date),
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = Icons.Outlined.CalendarToday.name,
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { newText ->
                        if (newText.length <= noteMaxLength) {
                            note = newText
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.weight_form_placeholder_note),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.weight_form_label_note),
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = Icons.Outlined.Description.name,
                        )
                    },
                    maxLines = 3,
                )
                Text(text = stringResource(R.string.optional))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedWeight = weightText.toDoubleOrNull()
                    if (parsedWeight != null) {
                        val newWeight = Weight(
                            id = weight?.id,
                            petId = petId,
                            value = parsedWeight,
                            notes = note
                        )
                        if (weight != null) {
                            weightViewModel.updateWeight(newWeight)
                        } else {
                            weightViewModel.addWeight(petId, newWeight)
                        }
                        onDismiss()
                    }
                }
            ) {
                Text(text = stringResource(R.string.weight_form_acept_btn))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.weight_form_cancel_btn))
            }
        }
    )
}

@Composable
fun SelectWeightUnitDialog(
    onUnitSelected: (WeightUnit) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Seleccionar unidad de peso")
        },
        text = {
            Column {
                WeightUnit.entries.forEach { unit ->
                    Text(
                        text = unit.displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUnitSelected(unit)
                                onDismiss()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
