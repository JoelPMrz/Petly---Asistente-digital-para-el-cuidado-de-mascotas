package com.example.petly.ui.screens.logged.pet


import android.net.Uri
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.data.models.Weight
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.components.PhotoPickerBottomSheet
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.convertWeight
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.truncate
import com.example.petly.viewmodel.PreferencesViewModel
import com.example.petly.viewmodel.WeightViewModel

@Composable
fun PetDetailScreen(
    analytics: AnalyticsManager,
    petId: String,
    navigateBack: () -> Unit,
    navigateToWeights: (String) -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val petState by petViewModel.petState.collectAsState()
    val weights by weightViewModel.weightsState.collectAsState()
    var weight by remember { mutableStateOf<Weight?>(null) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }


    LaunchedEffect(petId) {
        petViewModel.getPetById(petId)
        weightViewModel.getWeights(petId)
    }
    LaunchedEffect(weights) {
        weight = weights.lastOrNull()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (capturedImageUri != Uri.EMPTY) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = capturedImageUri
                        ),
                        contentDescription = stringResource(R.string.profile_pet_photo_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }else{
                    AsyncImage(
                        model = petState?.photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.pet_predeterminado),
                        error = painterResource(R.drawable.pet_predeterminado),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showPhotoPicker = true
                            }
                    )
                }

                IconCircle(
                    Icons.Rounded.ArrowBack,
                    onClick = navigateBack,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .align(Alignment.TopStart)
                )

                IconCircle(
                    icon = Icons.Rounded.Edit,
                    onClick = { },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                        .align(Alignment.TopEnd),
                    sizeIcon = 24.dp
                )

            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = petState?.name ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = petState?.birthDate.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Weigths(weight, petId, Modifier.weight(1f), navigateToWeights)
                Spacer(Modifier.width(10.dp))
                Weigths(weight, petId, Modifier.weight(1f), navigateToWeights)
            }

        }
    }

    if (showPhotoPicker) {
        PhotoPickerBottomSheet(
            onImageSelected = { uri ->
                capturedImageUri = uri
                petViewModel.updatePetProfilePhoto(petId, capturedImageUri, onSuccess = {
                    Toast.makeText(context, "Foto actualizada",  Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Toast.makeText(context, "No se ha actualizado",  Toast.LENGTH_SHORT).show()
                })
            },
            onDismiss = {
                showPhotoPicker = false
            }
        )
    }
}

@Composable
fun Weigths(
    weight: Weight?,
    petId: String,
    modifier: Modifier,
    navigateToWeights: (String) -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        preferencesViewModel.reloadUnitPreference()
    }

    val selectedUnit by preferencesViewModel.selectedUnit.collectAsState()
    var convertedWeight by remember { mutableStateOf("Agregar un peso") }
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(weight, selectedUnit) {
        if (weight != null) {
            convertedWeight =
                convertWeight(weight.value, weight.unit, selectedUnit).truncate(2).toString()
            dateString = formatLocalDateToString(weight.date)
        } else {
            convertedWeight = "Agregar un peso"
            dateString = ""
        }
    }

    Card(
        modifier = modifier
            .width(100.dp)
            .defaultMinSize(minHeight = 120.dp)
            .clickable { navigateToWeights(petId) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            IconCircle(
                icon = Icons.Outlined.MonitorWeight,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Peso actual", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = "$convertedWeight $selectedUnit",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = dateString,
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}




