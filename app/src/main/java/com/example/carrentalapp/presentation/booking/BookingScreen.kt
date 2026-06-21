package com.example.carrentalapp.presentation.booking

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carrentalapp.model.BusyPeriod
import com.example.carrentalapp.statemanagement.BookingUiState
import com.example.carrentalapp.statemanagement.BookingViewModel
import com.example.carrentalapp.statemanagement.CatalogViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    carId: String,
    onBookingConfirmed: () -> Unit,
    onBack: () -> Unit,
    bookingViewModel: BookingViewModel = viewModel(),
    catalogViewModel: CatalogViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by bookingViewModel.uiState.collectAsState()
    val catalogState by catalogViewModel.uiState.collectAsState()
    val estimatedCost by bookingViewModel.estimatedCost.collectAsState()
    val busyPeriods by bookingViewModel.busyPeriods.collectAsState()

    LaunchedEffect(Unit) {
        if (catalogState.cars.isEmpty()) catalogViewModel.loadCars(context)
        bookingViewModel.loadBusyPeriods(carId)
    }

    val car = catalogState.cars.find { it.id == carId }

    var startDateMs by remember { mutableStateOf<Long?>(null) }
    var endDateMs by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var conflicts by remember { mutableStateOf<List<BusyPeriod>>(emptyList()) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is BookingUiState.Success -> {
                Toast.makeText(context, "Бронирование подтверждено!", Toast.LENGTH_SHORT).show()
                onBookingConfirmed()
            }
            is BookingUiState.Error -> {
                Toast.makeText(context, (uiState as BookingUiState.Error).message, Toast.LENGTH_LONG).show()
                bookingViewModel.resetState()
            }
            else -> {}
        }
    }

    LaunchedEffect(startDateMs, endDateMs) {
        if (startDateMs != null && endDateMs != null && car != null) {
            bookingViewModel.calculateCost(startDateMs!!, endDateMs!!, car.baseDailyRate)
        }
    }

    if (showStartPicker) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = { TextButton(onClick = { showStartPicker = false }) { Text("OK") } }
        ) {
            val state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
            DatePicker(state = state)
            LaunchedEffect(state.selectedDateMillis) { state.selectedDateMillis?.let { startDateMs = it } }
        }
    }

    if (showEndPicker) {
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = { TextButton(onClick = { showEndPicker = false }) { Text("OK") } }
        ) {
            val state = rememberDatePickerState(initialSelectedDateMillis = (startDateMs ?: System.currentTimeMillis()) + 86_400_000L)
            DatePicker(state = state)
            LaunchedEffect(state.selectedDateMillis) { state.selectedDateMillis?.let { endDateMs = it } }
        }
    }

    // Диалог с занятыми датами вместо сухой ошибки
    if (conflicts.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { conflicts = emptyList() },
            icon = { Icon(Icons.Default.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Эти даты уже заняты") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Автомобиль недоступен в выбранный период. Уже забронировано:")
                    conflicts.forEach { p ->
                        Text("• ${formatRange(p)}", fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error)
                    }
                    Text("Выберите, пожалуйста, другие даты.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { Button(onClick = { conflicts = emptyList() }) { Text("Понятно") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бронирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            car?.let {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(it.modelName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("${it.baseDailyRate.toInt()} ₽ / день", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Подсказка о занятых датах
            if (busyPeriods.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text("Занятые даты", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        busyPeriods.forEach { p ->
                            Text("• ${formatRange(p)}", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }

            OutlinedButton(onClick = { showStartPicker = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(startDateMs?.let { "Начало: ${formatDate(it)}" } ?: "Выбрать дату начала")
            }

            OutlinedButton(onClick = { showEndPicker = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(endDateMs?.let { "Конец: ${formatDate(it)}" } ?: "Выбрать дату окончания")
            }

            estimatedCost?.let { cost ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Итого:", style = MaterialTheme.typography.titleMedium)
                        Text("${cost.toInt()} ₽", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (car != null && startDateMs != null && endDateMs != null) {
                        val overlap = findConflicts(startDateMs!!, endDateMs!!, busyPeriods)
                        if (overlap.isNotEmpty()) {
                            conflicts = overlap
                        } else {
                            bookingViewModel.createBooking(
                                car.id,
                                msToLocalDateTimeString(startDateMs!!, LocalTime.of(10, 0)),
                                msToLocalDateTimeString(endDateMs!!, LocalTime.of(23, 0))
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = startDateMs != null && endDateMs != null && uiState !is BookingUiState.Loading
            ) {
                if (uiState is BookingUiState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Забронировать", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun parseDate(iso: String): LocalDate =
    LocalDate.parse(iso.take(10))

/** Пересечение выбранного диапазона с занятыми периодами (на уровне дней). */
private fun findConflicts(startMs: Long, endMs: Long, busy: List<BusyPeriod>): List<BusyPeriod> {
    val selStart = Instant.ofEpochMilli(startMs).atZone(ZoneOffset.UTC).toLocalDate()
    val selEnd = Instant.ofEpochMilli(endMs).atZone(ZoneOffset.UTC).toLocalDate()
    return busy.filter { p ->
        val bStart = parseDate(p.startDateTime)
        val bEnd = parseDate(p.endDateTime)
        !selStart.isAfter(bEnd) && !selEnd.isBefore(bStart)
    }
}

private fun formatRange(p: BusyPeriod): String {
    val s = parseDate(p.startDateTime)
    val e = parseDate(p.endDateTime)
    return "${fmt(s)} – ${fmt(e)}"
}

private fun fmt(d: LocalDate): String =
    "${d.dayOfMonth.toString().padStart(2, '0')}.${d.monthValue.toString().padStart(2, '0')}.${d.year}"

private fun formatDate(ms: Long): String {
    val date = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
    return fmt(date)
}

private fun msToLocalDateTimeString(ms: Long, time: LocalTime): String {
    val date = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
    return LocalDateTime.of(date, time).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
