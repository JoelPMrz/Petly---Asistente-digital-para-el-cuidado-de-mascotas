package com.example.petly.ui.screens.logged.user

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MarkEmailUnread
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.data.models.User
import com.example.petly.ui.components.BaseOutlinedTextField
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.components.PasswordOutlinedTextField
import com.example.petly.ui.components.PhotoPickerBottomSheet
import com.example.petly.ui.screens.auth.changePassword
import com.example.petly.utils.AuthManager
import com.example.petly.viewmodel.PreferencesViewModel
import com.example.petly.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    auth: AuthManager,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userState by userViewModel.userState.collectAsState()
    var showCode by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var showUpdatePassword by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
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
            if (capturedImageUri != Uri.EMPTY) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = capturedImageUri
                    ),
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
                        .clickable { showPhotoPicker = true }
                )
            } else {
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
                        .clickable { showPhotoPicker = true }
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
                        showCode = true
                    },
                    modifier = Modifier,
                    title = stringResource(R.string.invitation_key),
                    icon = Icons.Rounded.VpnKey
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.invitations),
                    icon = Icons.Rounded.MarkEmailUnread
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {
                        showUpdatePassword = true
                    },
                    modifier = Modifier,
                    title = stringResource(R.string.update_password),
                    icon = Icons.Rounded.Password
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
                    title = stringResource(R.string.language),
                    icon = Icons.Rounded.Language
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileCard(
                    onClick = {

                    },
                    modifier = Modifier,
                    title = stringResource(R.string.notifications),
                    icon = Icons.Rounded.NotificationsActive
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
            }
        }

        if (showCode) {
            CodeBottomSheet(
                user = userState,
                onDismiss = {
                    showCode = false
                }
            )
        }

        if (showUpdatePassword) {
            UpdatePasswordBottomSheet(
                onDismiss = {
                    showUpdatePassword = false
                },
                auth = auth
            )
        }

        if (showPhotoPicker) {
            PhotoPickerBottomSheet(
                onImageSelected = { uri ->
                    capturedImageUri = uri
                    userViewModel.updateUserProfilePhoto(
                        auth.getCurrentUser()!!.uid,
                        capturedImageUri,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.updated_photo),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onFailure = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.not_updated_photo),
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                },
                onDismiss = {
                    showPhotoPicker = false
                }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBottomSheet(
    onDismiss: () -> Unit,
    user: User?,
) {
    val clipboardManager = LocalClipboardManager.current
    val code by remember { mutableStateOf(user?.id ?: "") }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .animateContentSize()
                    .padding(start = 15.dp, end = 15.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.invitation_key),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    1.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BaseOutlinedTextField(
                    value = code,
                    label = stringResource(R.string.key),
                    trailingIcon = Icons.Rounded.ContentCopy,
                    onClickTrailingIcon = {
                        clipboardManager.setText(AnnotatedString(code))
                    },
                    maxLines = 1,
                    readOnly = true
                ) {

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePasswordBottomSheet(
    onDismiss: () -> Unit,
    auth: AuthManager
) {
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatNewPassword by remember { mutableStateOf("") }

    var enableButton by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 15.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.invitation_key),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                1.dp
            )
            Spacer(modifier = Modifier.height(10.dp))
            PasswordOutlinedTextField(value = password) { password = it }
            Spacer(modifier = Modifier.height(10.dp))
            PasswordOutlinedTextField(value = newPassword) { newPassword = it }
            Spacer(modifier = Modifier.height(10.dp))
            PasswordOutlinedTextField(value = repeatNewPassword) { repeatNewPassword = it }
            Spacer(modifier = Modifier.height(20.dp)) // Espacio entre campos y botón

            Button(
                onClick = {
                    if (newPassword != repeatNewPassword) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (newPassword.length < 6) {
                        Toast.makeText(context, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    enableButton = false
                    scope.launch {
                        changePassword(
                            currentPassword = password,
                            newPassword = newPassword,
                            auth = auth,
                            context = context,
                            onSuccess = {
                                enableButton = true
                                onDismiss()
                            },
                            onError = {
                                enableButton = true
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = enableButton
            ) {
                Text(text = stringResource(R.string.edit))
            }
        }
    }

}



