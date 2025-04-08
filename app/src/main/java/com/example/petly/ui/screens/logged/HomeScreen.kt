package com.example.petly.ui.screens.logged

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager

@Composable
fun HomeScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    navigateToPetDetail:(String)-> Unit,
    navigateBack:()-> Unit,
    navigateToCreatePet:()-> Unit,
    petViewModel: PetViewModel =  hiltViewModel()
) {

    val pets by petViewModel.petsState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "HOME SCREEN", fontSize = 25.sp)
        Spacer(modifier = Modifier.height(20.dp))

        // Si no hay mascotas, muestra un mensaje
        if (pets.isEmpty()) {
            Text(text = "No tienes mascotas")
        } else {
            // Si hay mascotas, muestra una lista
            LazyColumn {
                items(pets) { pet ->
                    Pet(pet, petViewModel, navigateToPetDetail)
                }
            }
        }

        // Botón para crear una mascota
        Button(onClick = {
            navigateToCreatePet()
        }) {
            Text(text = "Crear mascota")
        }

        // Botón para cerrar sesión
        Button(onClick = {
            auth.singOut()
            navigateBack()
        }) {
            Text(text = "Cerrar sesión")
        }
    }

    LaunchedEffect(Unit) {
        petViewModel.getPets()
    }
}

@Composable
fun Pet(pet: Pet, petViewModel: PetViewModel, navigateToPetDetail: (String)-> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                pet.id?.let { navigateToPetDetail(it) }
            },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.pet_predeterminado),
                contentDescription = "Imagen de la mascota",
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column() {
                Text(text = pet.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pet.type
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pet.gender
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = {
                    // Eliminar la mascota
                    pet.id?.let { petViewModel.deletePet(it) }
                }) {
                    Text(text = "Eliminar mascota")
                }
            }
        }
    }
}



