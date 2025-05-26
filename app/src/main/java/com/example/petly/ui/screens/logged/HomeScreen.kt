package com.example.petly.ui.screens.logged

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.data.models.Pet
import com.example.petly.data.models.PetEvent
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.screens.logged.pet.AddVeterinaryVisitBottomSheet
import com.example.petly.ui.screens.logged.pet.DeletePetDialog
import com.example.petly.ui.screens.logged.pet.VeterinaryVisitCard
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AuthManager
import com.example.petly.utils.normalizeForSearch
import com.example.petly.viewmodel.EventsViewModel
import com.example.petly.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    //analytics: AnalyticsManager,
    auth: AuthManager,
    navigateToPetDetail: (String) -> Unit,
    navigateToAddPet: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    eventsViewModel: EventsViewModel = hiltViewModel()
) {
    val petList by petViewModel.petsState.collectAsState()
    val userState by userViewModel.userState.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val eventsPerDay by eventsViewModel.eventsPerDay.collectAsState()
    val events = eventsPerDay[selectedDate] ?: emptyList()
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }
    val filteredPetList = if (searchQuery.isBlank()) {
        petList
    } else {
        val normQuery = searchQuery.normalizeForSearch()
        petList.filter { pet ->
            pet.name.normalizeForSearch().contains(normQuery)
        }
    }
    val petsPagerState = rememberPagerState(initialPage = 0) { filteredPetList.size + 1 }
    val keyboardController = LocalSoftwareKeyboardController.current
    val days = remember {
        (0..6).map { offset ->
            LocalDate.now().plusDays(offset.toLong())
        }
    }
    val selectedPet by petViewModel.petState.collectAsState()
    var selectedVeterinaryVisit by remember { mutableStateOf<VeterinaryVisit?>(null) }


    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

    LaunchedEffect(Unit) {
        petViewModel.getAllPets()
    }

    LaunchedEffect(showSearch) {
        if (showSearch) {
            searchFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(petList) {
        if (petList.isNotEmpty()) {
            eventsViewModel.observeEventsForPetsInRange(petList, days)
        }
    }

    LaunchedEffect(selectedVeterinaryVisit) {
        selectedVeterinaryVisit?.let { visit ->
            petViewModel.getPetById(visit.petId)

        }
    }

    Scaffold(
        bottomBar = {
            MyNavigationAppBar(
                navigateToHome = {
                    navigateToHome()
                },
                navigateToCalendar = {
                    navigateToCalendar ()
                },
                navigateToUser = {
                    navigateToUser()
                },
                index = 1
            )
        },
        topBar = {
            HomeTopAppBar(
                userState?.photo,
                petList = petList,
                onSearch = {
                    showSearch = !showSearch
                },
                navigateToAddPet = {
                    navigateToAddPet()
                },
                navigateToUser = {
                    navigateToUser()
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 10.dp),
        ) {
            AnimatedVisibility(
                visible = showSearch
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 40.dp)
                        .offset(y = (-10).dp)
                        .height(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        ).border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .focusRequester(searchFocusRequester),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            fontSize = 8.sp,
                            lineHeight = 8.sp,
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 8.sp,
                        lineHeight = 8.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            showSearch = false
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            HorizontalPager(
                state = petsPagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 25.dp, end = 40.dp),
                pageSpacing = 16.dp,
                snapPosition = SnapPosition.Start
            ) { page ->
                when {
                    filteredPetList.isEmpty() && page == 0 -> {
                        AddPetCard(navigateToAddPet)
                    }
                    page < filteredPetList.size -> {
                        Pet(
                            pet = filteredPetList[page],
                            navigateToPetDetail = navigateToPetDetail,
                            auth = auth
                        )
                    }
                    else -> {
                        AddPetCard(navigateToAddPet)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Próximos eventos", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 25.dp), fontSize = 20.sp)

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { date ->
                    val isSelected = date == selectedDate
                    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .clickable { selectedDate = date }
                            .padding(vertical = 6.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).lowercase()
                                .replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                        Box(
                            contentAlignment = Alignment.TopEnd,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .height(20.dp)
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            if (!eventsPerDay[date].isNullOrEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .offset(x = 4.dp, y = 1.dp)
                                        .background(
                                            color = if (date == selectedDate)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onErrorContainer,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedContent(
                    targetState = events
                ) { currentEvents ->
                    if (currentEvents.isEmpty()) {
                        Text(
                            text = "No hay eventos para esta fecha",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    } else {
                        Column {
                            currentEvents.forEach { event ->
                                when (event) {
                                    is PetEvent.VeterinaryVisitEvent -> {
                                        VeterinaryVisitCard(event.visit, onClick = {
                                            selectedVeterinaryVisit = event.visit
                                        })
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedVeterinaryVisit != null) {
        selectedPet?.let { pet ->
            AddVeterinaryVisitBottomSheet(
                onDismiss = { selectedVeterinaryVisit = null },
                pet = pet,
                currentUser = userState,
                veterinaryVisit = selectedVeterinaryVisit,
            )
        }
    }
}

@Composable
fun Pet(
    pet: Pet,
    auth: AuthManager,
    navigateToPetDetail: (String) -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentUser by userViewModel.userState.collectAsState()
    val userRol by remember(currentUser, pet) {
        derivedStateOf {
            when {
                currentUser?.id == pet.creatorOwner -> 1
                pet.owners.contains(currentUser?.id) -> 2
                else -> 3
            }
        }
    }
    var showDeletePetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(MaterialTheme.shapes.extraLarge)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pet.photo)
                    .placeholder(R.drawable.pet_predeterminado)
                    .error(R.drawable.pet_predeterminado)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pet.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when (userRol) {
                            1 -> stringResource(R.string.creatorOwner)
                            2 -> stringResource(R.string.owner)
                            else -> stringResource(R.string.observer)
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }

    if (showDeletePetDialog) {
        DeletePetDialog(
            context = context,
            onDismiss = { showDeletePetDialog = false },
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
                    .height(180.dp)
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
                    text = stringResource(R.string.add),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.new_pet))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    photo: String?,
    petList: List<Pet>,
    onSearch: () -> Unit,
    navigateToAddPet: () -> Unit,
    navigateToUser: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        title = {
            Text(
                text = if (petList.size <= 1) stringResource(R.string.pet) else stringResource(R.string.pets),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
        },
        navigationIcon = {
            if (photo != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo)
                        .placeholder(R.drawable.default_user_profile_foto)
                        .error(R.drawable.default_user_profile_foto)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(35.dp)
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
                        .padding(8.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { navigateToUser() }
                )
            }

        },
        actions = {
            Row {
                IconCircle(
                    onClick = {
                        navigateToAddPet()
                    },
                    icon = Icons.Rounded.Add
                )
                Spacer(Modifier.width(5.dp))
                IconCircle(
                    onClick = {
                        onSearch()
                    },
                    icon = Icons.Rounded.Search
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
        )
    )
}










