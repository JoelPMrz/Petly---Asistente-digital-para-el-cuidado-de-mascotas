package com.example.petly.ui.screens.logged

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import com.example.petly.ui.components.BaseFAB
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.data.models.getAge
import com.example.petly.ui.components.IconSquare
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AuthManager

@Composable
fun HomeScreen(
    //analytics: AnalyticsManager,
    auth: AuthManager,
    navigateToPetDetail:(String)-> Unit,
    navigateBack:()-> Unit,
    navigateToAddPet:()-> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    petViewModel: PetViewModel =  hiltViewModel()
) {
    val pets by petViewModel.petsState.collectAsState()
    val state = rememberPagerState(initialPage = 0) { pets.size +1 }

    Scaffold (
        bottomBar = { MyNavigationAppBar(navigateToHome,navigateToCalendar,navigateToUser, 1) },
        floatingActionButton = {
            BaseFAB(
                onClick = {
                    navigateToAddPet()
                },
                imageVector = Icons.Rounded.Add
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 25.dp, end = 40.dp) ,
                pageSpacing = 16.dp ,
                snapPosition = SnapPosition.Start
            ) { page ->
                if (pets.isEmpty() && page == 0) {
                    Text(text = stringResource(R.string.empty_pet_list))
                } else if (page < pets.size) {
                    val pet = pets[page]

                    Pet(
                        pet = pet,
                        navigateToPetDetail = navigateToPetDetail,
                    )
                } else {
                    AddPetCard(navigateToAddPet)
                }
            }

            Button(onClick = {
                auth.singOut()
                navigateBack()
            }) {
                Text(text = stringResource(R.string.sign_out))
            }
        }

        LaunchedEffect(Unit) {
            petViewModel.getPets()
        }
    }

}

@Composable
fun Pet(pet: Pet, navigateToPetDetail: (String)-> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                pet.id?.let { navigateToPetDetail(it) }
            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            Image(
                painter = painterResource(R.drawable.pet_predeterminado),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-15).dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(start = 16.dp, end= 16.dp, top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pet.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    IconSquare(
                        icon = when (pet.gender) {
                            "Male" -> Icons.Rounded.Male
                            "Female" -> Icons.Rounded.Female
                            else -> Icons.Rounded.Transgender
                        },
                        onClick = { },
                        modifier = Modifier.size(20.dp),
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
}

@Composable
fun AddPetCard(
    navigateToAddPet:()-> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToAddPet()
            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.pet_predeterminado),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column{
                Text(text = "Añade una nuev amascota")

            }
        }
    }
}







