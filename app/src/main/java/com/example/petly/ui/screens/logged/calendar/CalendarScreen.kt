package com.example.petly.ui.screens.logged.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.petly.R
import com.example.petly.data.models.Event
import com.example.petly.data.models.Pet
import com.example.petly.data.models.PetEvent
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.models.getPet
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.screens.logged.pet.AddEventBottomSheet
import com.example.petly.ui.screens.logged.pet.AddVeterinaryVisitBottomSheet
import com.example.petly.ui.screens.logged.pet.EventCard
import com.example.petly.ui.screens.logged.pet.VeterinaryVisitCard
import com.example.petly.ui.viewmodel.PetViewModel
import com.example.petly.utils.AuthManager
import com.example.petly.viewmodel.EventsViewModel
import com.example.petly.viewmodel.UserViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun CalendarScreen(
    auth: AuthManager,
    navigateToHome: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToUser: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    eventsViewModel: EventsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val petList by petViewModel.petsState.collectAsState()
    val petListFiltered by petViewModel.filteredPetsState.collectAsState()
    val userState by userViewModel.userState.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val eventsPerDay by eventsViewModel.eventsPerDay.collectAsState()
    val events = eventsPerDay[selectedDate] ?: emptyList()
    val selectedPet by petViewModel.petState.collectAsState()
    var selectedVeterinaryVisit by remember { mutableStateOf<VeterinaryVisit?>(null) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showSelectPets by remember { mutableStateOf(false) }

    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthDays = remember(displayedMonth) {
        generateCalendarMonth(displayedMonth.atDay(1))
    }

    val daysWeek = context.resources?.getStringArray(R.array.days_of_week)?.toList() ?: emptyList()

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

    LaunchedEffect(Unit) {
        petViewModel.getAllPets()
    }

    LaunchedEffect(petList) {
        if (petList.isNotEmpty() && petListFiltered.isEmpty()) {
            petViewModel.updateFilteredPets(petList)
        }
    }

    LaunchedEffect(petListFiltered, monthDays) {
        if (petListFiltered.isNotEmpty()) {
            eventsViewModel.observeEventsForPetsInRange(petListFiltered, monthDays)
        }
    }

    LaunchedEffect(selectedVeterinaryVisit) {
        selectedVeterinaryVisit?.let { visit ->
            petViewModel.getPetById(visit.petId)
        }
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { event ->
            petViewModel.getPetById(event.petId)
        }
    }

    Scaffold(
        bottomBar = {
            MyNavigationAppBar(
                navigateToHome = navigateToHome,
                navigateToCalendar = navigateToCalendar,
                navigateToUser = navigateToUser,
                index = 0
            )
        },
        topBar = {
            CalendarTopAppBar(
                userState?.photo,
                navigateToUser = navigateToUser,
                onPetsSelect = {
                    showSelectPets = true
                },
                onTodayClick = {
                    displayedMonth = YearMonth.from(LocalDate.now())
                    selectedDate = LocalDate.now()
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 5.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconCircle(
                    icon = Icons.Rounded.ChevronLeft,
                    onClick = {
                        displayedMonth = displayedMonth.minusMonths(1)
                        selectedDate = null
                    },
                    backgroundColor = Color.Transparent
                )
                Text(
                    text = displayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() } +
                            " ${displayedMonth.year}"
                )
                IconCircle(
                    icon = Icons.Rounded.ChevronRight,
                    onClick = {
                        displayedMonth = displayedMonth.plusMonths(1)
                        selectedDate = null
                    },
                    backgroundColor = Color.Transparent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    daysWeek.forEach { dayText ->
                        Text(
                            text = dayText,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                monthDays.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            val isInCurrentMonth = date.month == displayedMonth.month
                            val isSelected = date == selectedDate
                            val textColor = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                !isInCurrentMonth -> Color.Gray
                                else -> MaterialTheme.colorScheme.onBackground
                            }
                            val alphaColor = when {
                                !isInCurrentMonth -> 0.4f
                                else -> 1f
                            }
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            val isToday = date == LocalDate.now()
                            var numberOfEvents = 0
                            var numberOfVeterinaryVisits = 0
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(backgroundColor)
                                    .border(
                                        width = if (isToday && !isSelected) 1.dp else 0.dp,
                                        color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDate = date }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            )
                            {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = textColor,
                                    fontSize = 14.sp
                                )

                                if (!eventsPerDay[date].isNullOrEmpty()) {
                                    eventsPerDay[date]?.forEach { event ->
                                        when (event) {
                                            is PetEvent.VeterinaryVisitEvent -> numberOfVeterinaryVisits++
                                            is PetEvent.NormalEvent -> numberOfEvents++
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (numberOfVeterinaryVisits > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(
                                                        color = Color(0xFFE573B1).copy(alpha = alphaColor),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }

                                        if (numberOfEvents > 0 && numberOfVeterinaryVisits > 0) {
                                            Spacer(Modifier.width(3.dp))
                                        }

                                        if (numberOfEvents > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(
                                                        color = Color(0xFFFFB74D).copy(alpha = alphaColor),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedContent(targetState = events) { currentEvents ->
                    if (selectedDate == null) {
                        Text(
                            text = stringResource(R.string.select_a_date),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        )
                    } else {
                        if (currentEvents.isEmpty()) {
                            Text(
                                text = stringResource(R.string.not_events),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        } else {
                            Column {
                                currentEvents.forEach { event ->
                                    val pet = event.getPet(petListFiltered)
                                    when (event) {
                                        is PetEvent.VeterinaryVisitEvent -> {
                                            VeterinaryVisitCard(
                                                event.visit,
                                                onClick = {
                                                    selectedVeterinaryVisit = event.visit
                                                },
                                                showImage = true,
                                                pet = pet
                                            )
                                        }

                                        is PetEvent.NormalEvent -> {
                                            EventCard(
                                                event.event,
                                                onClick = {
                                                    selectedEvent = event.event
                                                },
                                                showImage = true,
                                                pet = pet
                                            )
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
    }

    if (showSelectPets) {
        PetSelectionDialog(
            petList = petList,
            filteredPetList = petListFiltered,
            onDismiss = {
                showSelectPets = false
            }
        )
    }

    if (selectedVeterinaryVisit != null) {
        selectedVeterinaryVisit?.let { visit ->
            val pet = petList.find { it.id == visit.petId }
            if (pet != null) {
                AddVeterinaryVisitBottomSheet(
                    onDismiss = {
                        selectedVeterinaryVisit = null
                    },
                    pet = pet,
                    currentUser = userState,
                    veterinaryVisit = visit,
                )
            }
        }
    }

    if (selectedEvent != null) {
        selectedEvent?.let { event ->
            val pet = petList.find { it.id == event.petId }
            if (pet != null) {
                AddEventBottomSheet(
                    onDismiss = { selectedEvent = null },
                    pet = pet,
                    currentUser = userState,
                    event = event,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(
    photo: String?,
    onPetsSelect: () -> Unit,
    onTodayClick: () -> Unit,
    navigateToUser: () -> Unit,
) {
    val today = LocalDate.now()
    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
        title = {
            Text(
                text = stringResource(R.string.calendar),
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
            IconCircle(
                icon = Icons.Rounded.Pets,
                onClick = {
                    onPetsSelect()
                }
            )
            Spacer(Modifier.width(5.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable { onTodayClick() }
            ) {
                Text(
                    text = today.dayOfMonth.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
        )
    )
}

@Composable
fun PetSelectionDialog(
    petList: List<Pet>,
    filteredPetList: List<Pet>,
    onDismiss: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel()
) {
    var selectedPets by remember { mutableStateOf(filteredPetList.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.select_your_pets)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                petList.forEach { pet ->
                    val isSelected = selectedPets.contains(pet)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPets =
                                    if (isSelected) selectedPets - pet else selectedPets + pet
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pet.photo ?: R.drawable.pet_predeterminado)
                                .placeholder(R.drawable.pet_predeterminado)
                                .error(R.drawable.pet_predeterminado)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = pet.name,
                            modifier = Modifier.weight(1f)
                        )

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                selectedPets = if (it) selectedPets + pet else selectedPets - pet
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                petViewModel.updateFilteredPets(selectedPets.toList())
                onDismiss()
            }) {
                Text(stringResource(R.string.form_confirm_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.form_cancel_btn))
            }
        }
    )
}


fun generateCalendarMonth(baseDate: LocalDate): List<LocalDate> {
    val firstOfMonth = baseDate.withDayOfMonth(1)
    val lastOfMonth = baseDate.withDayOfMonth(baseDate.lengthOfMonth())

    val startDay = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endDay = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val totalDays = ChronoUnit.DAYS.between(startDay, endDay).toInt() + 1
    return (0 until totalDays).map { startDay.plusDays(it.toLong()) }
}


