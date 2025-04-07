package com.example.petly.ui.screens.logged.pet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.input.key.Key.Companion.T
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.data.models.Weight
import com.example.petly.ui.screens.logged.Pet
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.viewmodel.WeightViewModel

@Composable
fun PetDetailScreen(
    analytics: AnalyticsManager,
    petId: String,
    navigateBack:()-> Unit,
    navigateToWeights:(String)-> Unit,
    navigateToAddWeight:(String)-> Unit,
    petViewModel: PetViewModel =  hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel()
){
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val petState by petViewModel.petState.collectAsState()
    val weights by weightViewModel.weightsState.collectAsState()
    var weight by remember { mutableStateOf<Weight?>(null) }


    LaunchedEffect(petId) {
        petViewModel.getPetById(petId)
        weightViewModel.getWeights(petId)
    }
    LaunchedEffect(weights){
        weight = weights.firstOrNull()
    }

    Scaffold(
        topBar = {
            PetDetailTopAppBar(
                {
                    navigateBack()
                }
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
            Spacer(modifier = Modifier.height(10.dp))
            Weigths(weight, petId, navigateToWeights, navigateToAddWeight)
        }
    }
}

@Composable
fun Weigths(weight: Weight?, petId: String, navigateToWeights:(String)->Unit, navigateToAddWeights:(String)-> Unit){
    Card(
        modifier = Modifier
            .width(400.dp)
            .clickable {
                if(weight == null){
                    navigateToAddWeights(petId)
                }else{
                    navigateToWeights(petId)
                }
            },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(modifier = Modifier.padding(8.dp)){
            Text(text = if(weight != null ) weight.value.toString() else "Añadir un peso")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailTopAppBar(onClickIcon: (String) -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Detalle de la mascota",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onClickIcon("Atrás")
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = {
                onClickIcon("Menú desplegado")
            }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray)
    )
}