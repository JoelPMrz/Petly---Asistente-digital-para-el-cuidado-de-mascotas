package com.example.petly.ui.screens.logged

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Segment
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import com.example.petly.ui.components.BaseFAB
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.ImageResult
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.data.models.getAge
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.screens.logged.pet.DeletePetDialog
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AuthManager

@Composable
fun HomeScreen(
    //analytics: AnalyticsManager,
    navigateToPetDetail:(String)-> Unit,
    navigateToAddPet:()-> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    petViewModel: PetViewModel =  hiltViewModel()
) {
    val pets by petViewModel.petsState.collectAsState()
    val state = rememberPagerState(initialPage = 0) { pets.size +1 }

    LaunchedEffect(Unit) {
        petViewModel.getPets()
    }

    Scaffold (
        bottomBar = { MyNavigationAppBar(navigateToHome,navigateToCalendar,navigateToUser, 1) },
        topBar = { HomeTopAppBar() },
        /*
        floatingActionButton = {
            BaseFAB(
                onClick = {
                    navigateToAddPet()
                },
                imageVector = Icons.Rounded.Add
            )
        },
        floatingActionButtonPosition = FabPosition.End

         */
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 0.dp),
        ) {
            Row (
                Modifier.fillMaxWidth().padding(start = 30.dp, end = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text= "Mascotas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )
                Row{
                    IconCircle(
                        onClick = {
                            navigateToAddPet()
                        },
                        icon = Icons.Rounded.Add
                    )
                    Spacer(Modifier.width(5.dp))
                    IconCircle(
                        modifier = Modifier.clickable {

                        },
                        icon = Icons.Rounded.FilterAlt
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalPager(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 25.dp, end = 40.dp) ,
                pageSpacing = 16.dp ,
                snapPosition = SnapPosition.Start
            ) { page ->
                if (pets.isEmpty() && page == 0) {
                    AddPetCard(navigateToAddPet)
                } else if (page < pets.size) {
                    Pet(
                        pet = pets[page],
                        navigateToPetDetail = navigateToPetDetail,
                    )
                } else {
                    AddPetCard(navigateToAddPet)
                }
            }
        }
    }
}

@Composable
fun Pet(pet: Pet, navigateToPetDetail: (String)-> Unit) {
    val context = LocalContext.current
    var showDeletePetDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        pet.id?.let { navigateToPetDetail(it) }
                    },
                    onLongPress = {
                        showDeletePetDialog = true
                    }
                )
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pet.photo)
                    .placeholder(R.drawable.pet_predeterminado)
                    .error(R.drawable.pet_predeterminado)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .height(200.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-25).dp)
                    .clip(RoundedCornerShape( 30.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(start = 16.dp, end= 16.dp, top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pet.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    IconCircle(
                        icon = when (pet.gender) {
                            "Male" -> Icons.Rounded.Male
                            "Female" -> Icons.Rounded.Female
                            else -> Icons.Rounded.Transgender
                        },
                        modifier = Modifier.size(25.dp),
                        sizeIcon = 20.dp,
                        backgroundColor = when (pet.gender) {
                            "Male" -> MaterialTheme.colorScheme.primaryContainer
                            "Female" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        },
                        contentColor = when (pet.gender) {
                            "Male" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "Female" -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = pet.getAge())
            }
        }
    }
    if(showDeletePetDialog){
        DeletePetDialog(
            context = context,
            onDismiss = {showDeletePetDialog = false},
            navigateToHome = {},
            pet = pet
        )
    }
}

@Composable
fun AddPetCard(
    navigateToAddPet: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable {
                navigateToAddPet()
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .placeholder(R.drawable.pet_predeterminado)
                    .error(R.drawable.pet_predeterminado)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-25).dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Añadir",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "nueva mascota")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.profile_placeholder),
                contentDescription = "User profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {  }
            )
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}








