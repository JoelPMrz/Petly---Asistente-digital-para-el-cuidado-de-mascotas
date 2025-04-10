package com.example.petly.ui.screens.logged.weight

import android.R.attr.visible
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ArrowRight
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

@Composable
fun Weight(weight: Weight, weights: List<Weight>, weightViewModel: WeightViewModel, petId: String) {
    val difference: Double? = weightViewModel.comparePreviousWeight(weight, weights)
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        expanded = !expanded
                    },
                    onLongPress = {
                        weightViewModel.deleteWeight(petId = petId, weightId = weight.id.toString())
                    }
                )
            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(text = "${weight.value} kilos")
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
            AnimatedVisibility(expanded){
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = weight.notes ?: "No hay notas disponibles")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightsTopAppBar(navigateBack:()->Unit, petId: String, petName: String, weightViewModel: WeightViewModel) {
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
            ){
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
            if(showAddWeightDialog){
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWeightDialog(
    onDismiss: () -> Unit,
    petId: String,
    weightViewModel: WeightViewModel
) {
    var weightText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.create_weight_title)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Escribe algun comentario",
                            //color = colorResource(id = R.color.blue80)
                            )
                    },
                    label = {
                        Text(
                            text = "Comentario",
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic,
                            //color = colorResource(id = R.color.blue100)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = Icons.Outlined.Description.name,
                            //tint = colorResource(id = R.color.blue100)
                        )
                    },
                    /*
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = colorResource(id = R.color.blue50),
                        focusedBorderColor = colorResource(id = R.color.blue100),
                        unfocusedBorderColor = colorResource(id = R.color.blue50),
                        focusedTextColor = colorResource(id = R.color.blue100),
                        unfocusedTextColor = colorResource(id = R.color.blue100),
                    ),
                     */
                    maxLines = 2,
                )
                Text(text = "Opcional")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "20.0")
                    },
                    label = {
                        Text(
                            text = "Peso",
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedWeight = weightText.toDoubleOrNull()
                    if (parsedWeight != null) {
                        val newWeight = Weight(petId= petId, value = parsedWeight, notes = note)
                        weightViewModel.addWeight(petId, newWeight)
                        onDismiss()
                    }
                }
            ) {
                Text(text = "Añadir")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancelar")
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
