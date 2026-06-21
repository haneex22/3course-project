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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.carrentalapp.model.AdminCarRequest
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.presentation.common.CarLabels
import com.example.carrentalapp.statemanagement.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadAllCars() }

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
                        Text("Управление автопарком", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Добавить") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { StatsRow(uiState.cars) }
                items(uiState.cars) { car ->
                    AdminCarCard(car = car, onStatusChange = { newStatus ->
                        viewModel.updateCarStatus(car.id, newStatus)
                    })
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
private fun AdminCarCard(car: CarDto, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = CarLabels.carStatusColor(car.status)

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

                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Изменить статус")
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
            }
        }
    }
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
