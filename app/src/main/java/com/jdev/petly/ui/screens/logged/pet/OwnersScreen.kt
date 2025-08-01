package com.jdev.petly.ui.screens.logged.pet

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.jdev.petly.R
import com.jdev.petly.data.models.Pet
import com.jdev.petly.data.models.User
import com.jdev.petly.ui.components.BaseFAB
import com.jdev.petly.ui.components.BaseOutlinedTextField
import com.jdev.petly.ui.components.IconCircle
import com.jdev.petly.ui.components.pet.PetNotExistsDialog
import com.jdev.petly.ui.viewmodel.PetViewModel
import com.jdev.petly.utils.AuthManager
import com.jdev.petly.viewmodel.UserViewModel

@Composable
fun OwnersScreen(
    auth: AuthManager,
    petId: String,
    navigateToHome: () -> Unit,
    navigateBack: () -> Boolean,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var userClicked by remember { mutableStateOf<User?>(null) }
    var showUpdateCreatorOwner by remember { mutableStateOf(false) }
    var showAddPetOwner by remember { mutableStateOf(false) }
    var showUserClicked by remember { mutableStateOf(false) }
    val currentUserState by userViewModel.userState.collectAsState()
    val createdOwners by userViewModel.createdOwnerState.collectAsState()
    val ownersState by userViewModel.ownersState.collectAsState()
    val petState by petViewModel.petState.collectAsState()
    var showPetNotExistsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petId) {
        petViewModel.getObservedPet(
            petId,
            petNotExits = {
                showPetNotExistsDialog = true
            }
        )
        userViewModel.getUsersByRole(petId, "owners")
    }

    LaunchedEffect(petState?.creatorOwner) {
        petState?.creatorOwner?.let { userViewModel.getFlowCreatedOwner(it) }
    }

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }
    Scaffold(
        topBar = {
            OwnersTopBar(navigateBack)
        },
        floatingActionButton = {
            BaseFAB(
                onClick = {
                    petState?.id?.let {
                        petViewModel.doesPetExist(
                            petId = it,
                            exists = {  showAddPetOwner = true },
                            notExists = {
                                Toast.makeText(context, context.getString(R.string.pet_not_exists_dialog_title), Toast.LENGTH_SHORT).show()
                                        },
                            onFailure = {}
                        )
                    }
                },
                imageVector = Icons.Rounded.Add
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    if (ownersState.isEmpty()) {
                        Text(text = stringResource(R.string.not_ownsers_available))
                    }
                }

                items(ownersState, key = { it.id }) { user ->
                    OwnerCard(
                        user = user,
                        onClick = {
                            if (user.id == createdOwners?.id) {
                                showUpdateCreatorOwner = true
                            } else {
                                showUserClicked = true
                            }

                            userClicked = user
                        },
                        creatorOwner = createdOwners

                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showUserClicked) {
        EditOwnerBottomSheet(
            onDismiss = { showUserClicked = false },
            navigateToHome = { navigateToHome() },
            user = userClicked,
            pet = petState,
            currentUser = currentUserState
        )
    }

    if (showAddPetOwner) {
        AddPetOwnerBottomSheet(
            onDismiss = {
                showAddPetOwner = false
            },
            pet = petState
        )
    }

    if (showUpdateCreatorOwner) {
        UpdatePetCreatorOwnerBottomSheet(
            onDismiss = {
                showUpdateCreatorOwner = false
            },
            pet = petState,
            currentUser = currentUserState
        )
    }

    if (showPetNotExistsDialog) {
        PetNotExistsDialog(
            navigateTo = {
                navigateToHome()
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnersTopBar(navigateBack: () -> Boolean) {
    var showOwnersInfo by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        navigationIcon = {
            IconCircle(
                modifier = Modifier.size(35.dp),
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = {
                    navigateBack()
                }
            )
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.owners),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        },
        actions = {
            IconCircle(
                modifier = Modifier.size(24.dp),
                icon = Icons.Rounded.Info,
                onClick = {
                    showOwnersInfo = true
                }
            )
        }
    )

    if(showOwnersInfo){
        OwnersInfoDialog(
            onDismiss = {
                showOwnersInfo = false
            }
        )
    }
}

@Composable
fun OwnersInfoDialog(
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.owners_info_dialog_title))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ){
                    IconCircle(
                        modifier = Modifier.size(20.dp),
                        icon = Icons.Rounded.Pets,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(
                            R.string.owners_info_dialog_description,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ){
                    IconCircle(
                        modifier = Modifier.size(20.dp),
                        icon = Icons.Rounded.SupervisorAccount,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(
                            R.string.owners_info_dialog_description2,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ){
                    IconCircle(
                        modifier = Modifier.size(20.dp),
                        icon = Icons.Rounded.Group,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(
                            R.string.owners_info_dialog_description3,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ){
                    IconCircle(
                        modifier = Modifier.size(20.dp),
                        icon = Icons.Rounded.AdminPanelSettings,
                        backgroundColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(
                            R.string.owners_info_dialog_description4,
                        ),
                        color =  MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.form_confirm_btn))
            }
        },
    )
}

@Composable
fun OwnerCard(
    onClick: () -> Unit,
    creatorOwner: User?,
    user: User?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            if (creatorOwner?.id == user?.id) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top =  10.dp, end = 20.dp)
                        .size(18.dp),
                    imageVector = Icons.Rounded.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Column {
                Row(
                    Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user?.photo)
                            .placeholder(R.drawable.default_user_profile_foto)
                            .error(R.drawable.default_user_profile_foto)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = user?.name ?: "user${user?.id}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOwnerBottomSheet(
    onDismiss: () -> Unit,
    navigateToHome: () -> Unit,
    user: User?,
    currentUser: User?,
    pet: Pet?,
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var enableButton by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.manage_user, user?.name ?: "user${user?.id}"),
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        enableButton = false
                        pet?.id?.let {
                            if (pet.creatorOwner == user?.id) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.not_possible_change_creator),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                petViewModel.addPetObserver(
                                    petId = it,
                                    userIdToAdd = user!!.id,
                                    notPermission = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.permission_denied_observer),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    existsYet = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.already_observer),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.now_he_observer),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.role_could_not_be_changed),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            }
                        }
                        onDismiss()
                    }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconCircle(
                        icon = Icons.Outlined.Group,
                        backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.change_to_observer),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        enableButton = false
                        if (pet?.creatorOwner == user?.id) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.not_possible_delete_creator),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            pet?.id?.let {
                                petViewModel.deletePetOwner(
                                    petId = it,
                                    userIdToRemove = user!!.id,
                                    notPermission = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.permission_denied_observer),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    notExistsYet = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.already_owner),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    onSuccess = {
                                        if (user.id == currentUser?.id && !pet.observers.contains(
                                                user.id
                                            )
                                        ) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.you_have_been_removed_as_owner),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navigateToHome()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.owner_deleted),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.not_possible_delete),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            }
                        }
                        onDismiss()
                    }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconCircle(
                        icon = Icons.Outlined.PersonOff,
                        backgroundColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.delete_rol),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetOwnerBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    userViewModel: UserViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    var incompleteUser by remember { mutableStateOf(false) }

    var enableButton by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.add_petOwner),
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
                value = userId,
                label = stringResource(R.string.invitation_key),
                maxLines = 1,
                isError = incompleteUser,
                isRequired = true
            ) {
                userId = it
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    enableButton = false
                    if (userId.isBlank()) {
                        incompleteUser = userId.isBlank()
                        enableButton = true
                        return@Button
                    } else {
                        pet?.id?.let {
                            userViewModel.checkUserExists(
                                userId = userId,
                                onSuccess = {
                                    petViewModel.addPetOwner(
                                        petId = it,
                                        userIdToAdd = userId,
                                        notPermission = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.permission_denied_observer),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        existsYet = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.already_owner),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        onSuccess = {
                                            if (pet.observers.contains(userId)) {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.not_observer_new_owner),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.new_owner),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.could_not_add),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                },
                                notExist = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.user_not_exist),
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.could_not_add),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )

                        }
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = enableButton
            ) {
                Text(
                    text = stringResource(R.string.add)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePetCreatorOwnerBottomSheet(
    onDismiss: () -> Unit,
    pet: Pet?,
    currentUser: User?,
    userViewModel: UserViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    var incompleteUser by remember { mutableStateOf(false) }

    var enableButton by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.update_creatorOwner),
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
                value = userId,
                label = stringResource(R.string.invitation_key),
                maxLines = 1,
                isError = incompleteUser,
                isRequired = true
            ) {
                userId = it
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    enableButton = false
                    if (userId.isBlank()) {
                        incompleteUser = userId.isBlank()
                        enableButton = true
                        return@Button
                    } else if (pet?.creatorOwner != currentUser?.id) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.authorized_for_creator),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        pet?.id?.let {
                            userViewModel.checkUserExists(
                                userId = userId,
                                onSuccess = {
                                    petViewModel.updateCreatorOwner(
                                        petId = it,
                                        newCreatorOwnerId = userId,
                                        notPermission = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.authorized_for_creator),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        isCurrentCreatorOwner = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.already_creator),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.new_creator),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.role_could_not_be_changed),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                },
                                notExist = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.user_not_exist),
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.could_not_add),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = enableButton
            ) {
                Text(
                    text = stringResource(R.string.add)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}