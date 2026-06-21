package com.example.carrentalapp.presentation.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.carrentalapp.model.AdminBookingDto
import com.example.carrentalapp.model.AdminCarRequest
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.model.UnverifiedClient
import com.example.carrentalapp.presentation.common.CarLabels
import com.example.carrentalapp.statemanagement.AdminUiState
import com.example.carrentalapp.statemanagement.AdminViewModel
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.CallMade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadAllCars()
        viewModel.loadUnverifiedClients()
        viewModel.loadAllBookings()
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    if (showAddDialog) {
        AddCarDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { request ->
                viewModel.addCar(request)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Панель управления", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Управление автопарком и бронированиями", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadAllCars()
                        viewModel.loadUnverifiedClients()
                        viewModel.loadAllBookings()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Добавить") },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading || uiState.isClientsLoading || uiState.isBookingsLoading,
            onRefresh = {
                viewModel.loadAllCars()
                viewModel.loadUnverifiedClients()
                viewModel.loadAllBookings()
            },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Автопарк (${uiState.cars.size})") },
                        icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Бронирования (${uiState.bookings.size})") },
                        icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Клиенты (${uiState.unverifiedClients.size})") },
                        icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                when (selectedTab) {
                    0 -> CarsTab(uiState, viewModel)
                    1 -> BookingsTab(uiState, viewModel)
                    2 -> ClientsTab(uiState, viewModel)
                }
            }
        }
    }
}

@Composable
private fun CarsTab(uiState: AdminUiState, viewModel: AdminViewModel) {
    if (uiState.isLoading && uiState.cars.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StatsRow(uiState.cars) }
            items(uiState.cars) { car ->
                AdminCarCard(
                    car = car,
                    onStatusChange = { newStatus -> viewModel.updateCarStatus(car.id, newStatus) },
                    onDelete = { viewModel.deleteCar(car.id) },
                    onEdit = { request -> viewModel.updateCar(car.id, request) }
                )
            }
            if (uiState.cars.isEmpty()) {
                item {
                    Text("Автопарк пуст. Добавьте первый автомобиль.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun BookingsTab(uiState: AdminUiState, viewModel: AdminViewModel) {
    when {
        uiState.isBookingsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.bookings.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Нет бронирований",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Бронирования появятся здесь после оформления",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Все бронирования",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Всего: ${uiState.bookings.size}", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(uiState.bookings) { booking ->
                    AdminBookingCard(
                        booking = booking,
                        onCancel = { viewModel.cancelBooking(booking.id) },
                        onHandover = { mileage, fuel -> viewModel.handoverCar(booking.id, mileage, fuel) },
                        onReturn = { mileage, fuel -> viewModel.returnCar(booking.id, mileage, fuel) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientsTab(uiState: AdminUiState, viewModel: AdminViewModel) {
    when {
        uiState.isClientsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.unverifiedClients.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Все клиенты верифицированы",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Новых заявок на верификацию нет",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Заявки на верификацию",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("${uiState.unverifiedClients.size} клиент(ов) ожидают подтверждения",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(uiState.unverifiedClients) { client ->
                    UnverifiedClientCard(
                        client = client,
                        onVerify = { viewModel.verifyClient(client.userId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminBookingCard(
    booking: AdminBookingDto,
    onCancel: () -> Unit,
    onHandover: ((Long, Int) -> Unit)? = null,
    onReturn: ((Long, Int) -> Unit)? = null
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showHandoverDialog by remember { mutableStateOf(false) }
    var showReturnDialog by remember { mutableStateOf(false) }

    val statusColor = when (booking.status) {
        "PENDING" -> MaterialTheme.colorScheme.tertiary
        "CONFIRMED" -> MaterialTheme.colorScheme.secondary
        "ACTIVE" -> MaterialTheme.colorScheme.primary
        "CANCELLED" -> MaterialTheme.colorScheme.error
        "COMPLETED" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outline
    }
    val statusLabel = when (booking.status) {
        "PENDING" -> "Ожидает"
        "CONFIRMED" -> "Подтверждено"
        "ACTIVE" -> "Активно"
        "CANCELLED" -> "Отменено"
        "COMPLETED" -> "Завершено"
        else -> booking.status
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Отменить бронирование?", fontWeight = FontWeight.Bold) },
            text = { Text("Бронирование ${booking.carModelName} от ${booking.startDateTime.take(10)} будет отменено.") },
            confirmButton = {
                Button(onClick = { showCancelDialog = false; onCancel() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Отменить")
                }
            },
            dismissButton = { TextButton(onClick = { showCancelDialog = false }) { Text("Назад") } }
        )
    }

    if (showHandoverDialog) {
        HandoverDialog(
            booking = booking,
            onDismiss = { showHandoverDialog = false },
            onConfirm = { mileage, fuel -> showHandoverDialog = false; onHandover?.invoke(mileage, fuel) }
        )
    }

    if (showReturnDialog) {
        ReturnDialog(
            booking = booking,
            onDismiss = { showReturnDialog = false },
            onConfirm = { mileage, fuel -> showReturnDialog = false; onReturn?.invoke(mileage, fuel) }
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.carModelName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Text(statusLabel, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(booking.clientEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text("Заезд", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(booking.startDateTime.take(16).replace("T", " "), style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text("Выезд", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(booking.endDateTime.take(16).replace("T", " "), style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("${booking.amount.toInt()} ${booking.currency}/день",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f))

                if (booking.status == "CONFIRMED" && onHandover != null) {
                    FilledTonalButton(
                        onClick = { showHandoverDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Handyman, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Выдать", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (booking.status == "ACTIVE" && onReturn != null) {
                    FilledTonalButton(
                        onClick = { showReturnDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CallMade, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Принять", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (booking.status == "PENDING") {
                    FilledTonalButton(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Отменить", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun UnverifiedClientCard(client: UnverifiedClient, onVerify: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(client.email, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                if (!client.passportSeries.isNullOrBlank() || !client.passportNumber.isNullOrBlank()) {
                    Text("Паспорт: ${client.passportSeries ?: ""} ${client.passportNumber ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (!client.licenseNumber.isNullOrBlank()) {
                    Text("ВУ: ${client.licenseNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("Регистрация: ${client.registrationDate.take(10)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            FilledTonalButton(onClick = onVerify) {
                Text("Подтвердить")
            }
        }
    }
}

@Composable
private fun StatsRow(cars: List<CarDto>) {
    val total = cars.size
    val available = cars.count { it.status == "AVAILABLE" }
    val busy = cars.count { it.status == "RESERVED" || it.status == "RENTED" }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Всего", total.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        StatCard("Свободно", available.toString(), CarLabels.carStatusColor("AVAILABLE"), Modifier.weight(1f))
        StatCard("Занято", busy.toString(), CarLabels.carStatusColor("RESERVED"), Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AdminCarCard(car: CarDto, onStatusChange: (String) -> Unit, onDelete: () -> Unit, onEdit: ((AdminCarRequest) -> Unit)? = null) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val statusColor = CarLabels.carStatusColor(car.status)

    if (showEditDialog) {
        EditCarDialog(
            car = car,
            onDismiss = { showEditDialog = false },
            onConfirm = { request ->
                onEdit?.invoke(request)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить автомобиль?", fontWeight = FontWeight.Bold) },
            text = { Text("Автомобиль ${car.modelName} (${car.licensePlate}) будет удалён навсегда.") },
            confirmButton = {
                Button(onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Удалить")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") } }
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                if (!car.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = car.imageUrl,
                        contentDescription = car.modelName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    }
                }
                Surface(
                    modifier = Modifier.padding(10.dp).align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(CarLabels.carClass(car.carClass),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(car.modelName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(car.licensePlate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("VIN: ${car.vin}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${car.baseDailyRate.toInt()} ₽/день",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary)

                    Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.12f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(statusColor))
                            Text(CarLabels.carStatus(car.status), style = MaterialTheme.typography.labelSmall,
                                color = statusColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showEditDialog = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать", modifier = Modifier.size(20.dp))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = "Статус", modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            CarLabels.carStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(CarLabels.carStatus(status)) },
                                    onClick = { expanded = false; onStatusChange(status) }
                                )
                            }
                        }
                    }
                    FilledTonalButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCarDialog(car: CarDto, onDismiss: () -> Unit, onConfirm: (AdminCarRequest) -> Unit) {
    var modelName by remember { mutableStateOf(car.modelName) }
    var carClass by remember { mutableStateOf(car.carClass) }
    var licensePlate by remember { mutableStateOf(car.licensePlate) }
    var vin by remember { mutableStateOf(car.vin) }
    var dailyRate by remember { mutableStateOf(car.baseDailyRate.toInt().toString()) }
    var imageUrl by remember { mutableStateOf(car.imageUrl ?: "") }
    var classExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = modelName, onValueChange = { modelName = it },
                    label = { Text("Модель") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                ExposedDropdownMenuBox(expanded = classExpanded, onExpandedChange = { classExpanded = it }) {
                    OutlinedTextField(
                        value = CarLabels.carClass(carClass),
                        onValueChange = {}, readOnly = true,
                        label = { Text("Класс") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                        CarLabels.carClasses.forEach { (code, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { carClass = code; classExpanded = false })
                        }
                    }
                }

                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = it },
                    label = { Text("Гос. номер") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = vin, onValueChange = { vin = it },
                    label = { Text("VIN (17 символов)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = dailyRate, onValueChange = { dailyRate = it.filter { c -> c.isDigit() } },
                    label = { Text("Тариф, ₽/день") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it },
                    label = { Text("Ссылка на фото (необязательно)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val rate = dailyRate.toDoubleOrNull() ?: return@Button
                if (modelName.isBlank() || licensePlate.isBlank() || vin.isBlank()) return@Button
                onConfirm(AdminCarRequest(vin.trim(), licensePlate.trim(), modelName.trim(), carClass, rate, imageUrl.takeIf { it.isNotBlank() }?.trim()))
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun HandoverDialog(
    booking: AdminBookingDto,
    onDismiss: () -> Unit,
    onConfirm: (Long, Int) -> Unit
) {
    var mileage by remember { mutableStateOf("") }
    var fuelLevel by remember { mutableStateOf("100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выдать автомобиль", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Бронирование: ${booking.carModelName}")
                Text("Клиент: ${booking.clientEmail}", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it.filter { c -> c.isDigit() } },
                    label = { Text("Пробег, км") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fuelLevel,
                    onValueChange = { f ->
                        val filtered = f.filter { c -> c.isDigit() }
                        if (filtered.toIntOrNull()?.let { it in 0..100 } != false) {
                            fuelLevel = filtered
                        }
                    },
                    label = { Text("Уровень топлива, %") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val m = mileage.toLongOrNull() ?: return@Button
                val f = fuelLevel.toIntOrNull() ?: return@Button
                onConfirm(m, f)
            }) { Text("Выдать") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun ReturnDialog(
    booking: AdminBookingDto,
    onDismiss: () -> Unit,
    onConfirm: (Long, Int) -> Unit
) {
    var mileage by remember { mutableStateOf("") }
    var fuelLevel by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Принять возврат", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Автомобиль: ${booking.carModelName}")
                Text("Клиент: ${booking.clientEmail}", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it.filter { c -> c.isDigit() } },
                    label = { Text("Итоговый пробег, км") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fuelLevel,
                    onValueChange = { f ->
                        val filtered = f.filter { c -> c.isDigit() }
                        if (filtered.toIntOrNull()?.let { it in 0..100 } != false) {
                            fuelLevel = filtered
                        }
                    },
                    label = { Text("Уровень топлива, %") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val m = mileage.toLongOrNull() ?: return@Button
                val f = fuelLevel.toIntOrNull() ?: return@Button
                onConfirm(m, f)
            }) { Text("Принять") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCarDialog(onDismiss: () -> Unit, onConfirm: (AdminCarRequest) -> Unit) {
    var modelName by remember { mutableStateOf("") }
    var carClass by remember { mutableStateOf("ECONOMY") }
    var licensePlate by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var dailyRate by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var classExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый автомобиль", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = modelName, onValueChange = { modelName = it },
                    label = { Text("Модель") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                ExposedDropdownMenuBox(expanded = classExpanded, onExpandedChange = { classExpanded = it }) {
                    OutlinedTextField(
                        value = CarLabels.carClass(carClass),
                        onValueChange = {}, readOnly = true,
                        label = { Text("Класс") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                        CarLabels.carClasses.forEach { (code, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { carClass = code; classExpanded = false })
                        }
                    }
                }

                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = it },
                    label = { Text("Гос. номер") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = vin, onValueChange = { vin = it },
                    label = { Text("VIN (17 символов)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = dailyRate, onValueChange = { dailyRate = it.filter { c -> c.isDigit() } },
                    label = { Text("Тариф, ₽/день") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it },
                    label = { Text("Ссылка на фото (необязательно)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val rate = dailyRate.toDoubleOrNull() ?: return@Button
                if (modelName.isBlank() || licensePlate.isBlank() || vin.isBlank()) return@Button
                onConfirm(AdminCarRequest(vin.trim(), licensePlate.trim(), modelName.trim(), carClass, rate, imageUrl.takeIf { it.isNotBlank() }?.trim()))
            }) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
