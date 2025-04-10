package com.example.petly.ui.screens.logged.pet


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Weight
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
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            Image(
                painter = painterResource(R.drawable.pet_predeterminado),
                contentDescription = "Imagen de la mascota",
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text= petState?.name ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text= "22 de marzo de 2020",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Weigths(weight, petId, navigateToWeights, navigateToAddWeight)
        }
    }
}

@Composable
fun Weigths(weight: Weight?, petId: String, navigateToWeights:(String)->Unit, navigateToAddWeights:(String)-> Unit){
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable {
                navigateToWeights(petId)
            },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.large,
        /*
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        )

         */
    ) {
        Column(modifier = Modifier.padding(8.dp)){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Row(){
                    Icon(
                        imageVector = Icons.Outlined.MonitorWeight,
                        contentDescription = "Weight icon",
                        //tint = colorResource(id = R.color.blue100)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text= "Peso", fontWeight = FontWeight.W500)
                }
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Back", modifier = Modifier.padding(top = 4.dp))
            }
            Text(text = weight?.value?.toString() ?: "Agregar un peso")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailTopAppBar(onClickIcon: (String) -> Unit) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.pet_detail_screen_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.W400,
                fontStyle = FontStyle.Italic,
                //color = Color.DarkGray
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
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit pet")
            }
        },
        //colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}