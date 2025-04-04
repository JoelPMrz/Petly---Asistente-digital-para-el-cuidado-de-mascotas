package com.example.petly.ui.screens.login.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.petly.R
import com.example.petly.data.models.PetModel
import com.example.petly.navegation.CreatePet
import com.example.petly.navegation.Home
import com.example.petly.navegation.Login
import com.example.petly.utils.AnalyticsManager
import com.example.petly.utils.AuthManager
import com.example.petly.utils.RealtimeManager

@Composable
fun HomeScreen(
    analytics: AnalyticsManager,
    auth: AuthManager,
    realtime: RealtimeManager,
    navigation: NavController
) {

    val onConfirmLogOut: () -> Unit = {
        auth.singOut()
        navigation.navigate(Login) {
            popUpTo(Home) {
                inclusive = true
            }
        }
    }

    val pets by realtime.getPetsFlows().collectAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "HOME SCREEN", fontSize = 25.sp)
        Spacer(modifier = Modifier.height(20.dp))
        if (pets.isEmpty()) {
            Text(text = "No tienes macotas")
        } else {
            LazyColumn {
                pets.forEach { pet ->
                    item {
                        Pet(pet, realtime)
                    }
                }
            }
        }
        Button(onClick = {
            navigation.navigate(CreatePet)
        }) {
            Text(text = "Crear mascota")
        }
        Button(onClick = {
            onConfirmLogOut()
        }) {
            Text(text = "Cerrar sesión")
        }
    }
}


@Composable
fun Pet(pet: PetModel, realtime: RealtimeManager) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
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
                    realtime.deletePet(pet.key ?: "")
                }) {
                    Text(text = "Eliminar mascota")
                }
            }
        }
    }
}


