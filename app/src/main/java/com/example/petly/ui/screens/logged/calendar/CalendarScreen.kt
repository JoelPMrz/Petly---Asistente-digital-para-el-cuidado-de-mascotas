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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.petly.data.models.PetEvent
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.models.getPet
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.MyNavigationAppBar
import com.example.petly.ui.screens.logged.pet.AddVeterinaryVisitBottomSheet
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
    val petList by petViewModel.petsState.collectAsState()
    val userState by userViewModel.userState.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val eventsPerDay by eventsViewModel.eventsPerDay.collectAsState()
    val events = eventsPerDay[selectedDate] ?: emptyList()
    val selectedPet by petViewModel.petState.collectAsState()
    var selectedVeterinaryVisit by remember { mutableStateOf<VeterinaryVisit?>(null) }

    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthDays = remember(displayedMonth) {
        generateCalendarMonth(displayedMonth.atDay(1))
    }

    LaunchedEffect(true) {
        val uid = auth.getCurrentUser()?.uid
        if (uid != null) {
            userViewModel.getUserFlowById(uid)
        }
    }

    LaunchedEffect(Unit) {
        petViewModel.getAllPets()
    }

    LaunchedEffect(petList, monthDays) {
        if (petList.isNotEmpty()) {
            eventsViewModel.observeEventsForPetsInRange(petList, monthDays)
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
                .padding(vertical = 10.dp),
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
                    }
                )
                Text(
                    text = displayedMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))
                        .replaceFirstChar { it.uppercase() } +
                            " ${displayedMonth.year}"
                )
                IconCircle(
                    icon = Icons.Rounded.ChevronRight,
                    onClick = {
                        displayedMonth = displayedMonth.plusMonths(1)
                        selectedDate = null
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { dayText ->
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
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            val isToday = date == LocalDate.now()
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(backgroundColor)
                                    .border(
                                        width = if (isToday && !isSelected) 2.dp else 0.dp,
                                        color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDate = date }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            )
                            {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = textColor,
                                    fontSize = 14.sp
                                )

                                if (!eventsPerDay[date].isNullOrEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onErrorContainer,
                                                shape = CircleShape
                                            )
                                    )
                                }
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
                AnimatedContent(targetState = events) { currentEvents ->
                    if(selectedDate == null){
                        Text(
                            text = "Selecciona una fecha",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }else{
                        if (currentEvents.isEmpty()) {
                            Text(
                                text = "No hay eventos para esta fecha",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        } else {
                            Column {
                                currentEvents.forEach { event ->
                                    val pet = event.getPet(petList)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(
    photo: String?,
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
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable {
                        onTodayClick()
                    }
            ) {
                Text(
                    text = today.dayOfMonth.toString(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
        )
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


