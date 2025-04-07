package com.example.petly.ui.screens.logged.weight

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.data.models.Weight
import com.example.petly.data.models.WeightUnit
import com.example.petly.ui.screens.logged.Pet
import com.example.petly.ui.screens.logged.pet.MyFloatingActionButton
import com.example.petly.ui.screens.logged.pet.MyNavigationAppBar
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.viewmodel.WeightViewModel

@Composable
fun WeightsScreen(
    analytics: AnalyticsManager,
    petId: String,
    navigateBack:()-> Unit,
    petViewModel: PetViewModel =  hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel()
){

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val petState by petViewModel.petState.collectAsState()
    val weights by weightViewModel.weightsState.collectAsState()

    LaunchedEffect(petId) {
        petViewModel.getPetById(petId)
        weightViewModel.getWeights(petId)
    }

    //val petName: String = petState?.name.toString() :? "Nombre desconocido"

    Scaffold(
        topBar = {
            WeightsTopAppBar(
                navigateBack,
                petState?.name.toString()
            )
        },
        bottomBar = { MyNavigationAppBar() },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = { MyFloatingActionButton() },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(text= petState?.name ?: "")
            if (weights.isEmpty()) {
                Text(text = "No tienes pesos")
            } else {
                LazyColumn (modifier = Modifier.padding(10.dp)) {
                    items(weights) { weight ->
                        Weight(weight)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun Weight(weight: Weight){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(8.dp)){
            Text(text = "${weight.value} kilos" )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = weight.notes.toString())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightsTopAppBar(navigateBack:()->Unit, petName: String) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf("") }
    TopAppBar(
        title = {
            Text(
                "Pesos de $petName",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic
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
            Text(text= selectedUnit)
            Spacer(modifier = Modifier.height(5.dp))
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(imageVector = Icons.Rounded.Scale, contentDescription = "Scale icon")
            }
            if (showDialog) {
                SelectWeightUnitDialog(
                    onUnitSelected = { unit ->
                        selectedUnit = unit.toString()
                        println("Unidad seleccionada: $unit")
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray)
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
                WeightUnit.values().forEach { unit ->
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
