package com.example.petly.ui.screens.logged.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.utils.AuthManager
import com.example.petly.viewmodel.PreferencesViewModel
import com.example.petly.viewmodel.UserViewModel

@Composable
fun UserScreen(
    auth: AuthManager,
    userId: String?,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val userState by userViewModel.userState.collectAsState()
    val isDarkMode by preferencesViewModel.isDarkMode.collectAsState()


    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserById(uid)
        }
    }

    Scaffold(
        bottomBar = { MyNavigationAppBar(navigateToHome, navigateToCalendar, navigateToUser, 2) }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            if (userState?.photo != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userState?.photo)
                        .placeholder(R.drawable.default_user_profile_foto)
                        .error(R.drawable.default_user_profile_foto)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(135.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { navigateToUser() }
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.default_user_profile_foto),
                    contentDescription = "User profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(135.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { navigateToUser() }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = userState?.name ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(userState?.email ?: "", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(15.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    stringResource(R.string.profile),
                    modifier = Modifier.padding(start = 5.dp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.edit_user_profile),
                    icon = Icons.Rounded.HowToReg
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {
                        auth.singOut()
                        navigateBack()
                        userViewModel.clearUser()
                    },
                    modifier = Modifier,
                    title = stringResource(R.string.sign_out),
                    icon = Icons.Rounded.Logout
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    stringResource(R.string.settings),
                    modifier = Modifier.padding(start = 5.dp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.notifications),
                    icon = Icons.Rounded.Notifications
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {
                        preferencesViewModel.setDarkMode(!isDarkMode)
                    },
                    modifier = Modifier,
                    title = if (isDarkMode) stringResource(R.string.dark_mode) else stringResource(R.string.light_mode),
                    icon = if (isDarkMode) Icons.Rounded.Bedtime else Icons.Rounded.LightMode
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.clear_cache),
                    icon = Icons.Rounded.CleaningServices
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.clear_cache),
                    icon = Icons.Rounded.CleaningServices
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.clear_cache),
                    icon = Icons.Rounded.CleaningServices
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    userViewModel: UserViewModel = hiltViewModel(),
    onClick: () -> Unit,
    modifier: Modifier,
    title: String,
    icon: ImageVector
) {
    val userState by userViewModel.userState.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCircle(
                    icon = icon,
                    modifier = Modifier.size(30.dp),
                    sizeIcon = 20.dp,
                    backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondaryContainer
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    }
}