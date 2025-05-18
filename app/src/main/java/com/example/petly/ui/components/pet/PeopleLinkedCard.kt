package com.example.petly.ui.components.pet

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.data.models.User
import com.example.petly.ui.components.IconCircle
import com.example.petly.viewmodel.UserViewModel

@Composable
fun PeopleLinkedCard(
    pet: Pet?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    role: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val users by when (role) {
        "owners" -> userViewModel.ownersState.collectAsState()
        "observers" -> userViewModel.observersState.collectAsState()
        else -> remember { mutableStateOf(emptyList()) }
    }

    LaunchedEffect(pet?.id, role) {
        pet?.id?.let {
            userViewModel.getUsersByRole(it, role)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = if (role == "owners") Icons.Outlined.SupervisorAccount else Icons.Outlined.Group,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (role == "owners") stringResource(R.string.owners) else stringResource(R.string.observers),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ){
                if(users.isEmpty()){
                    Text("No dispone")
                }else{
                    users.forEach { user ->
                        PersonLinked(user)
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun PersonLinked(user: User) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(user.photo)
            .placeholder(R.drawable.default_user_profile_foto)
            .error(R.drawable.default_user_profile_foto)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.5f),
                shape = CircleShape
            )
    )
}
