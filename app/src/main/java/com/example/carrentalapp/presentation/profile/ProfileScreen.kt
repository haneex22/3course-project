package com.example.carrentalapp.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carrentalapp.model.ReservationDto
import com.example.carrentalapp.presentation.common.CarLabels
import com.example.carrentalapp.statemanagement.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onAdminClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var cancelTarget by remember { mutableStateOf<ReservationDto?>(null) }

    LaunchedEffect(Unit) { viewModel.load(context) }

    LaunchedEffect(uiState.message, uiState.error) {
        (uiState.message ?: uiState.error)?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    cancelTarget?.let { res ->
        AlertDialog(
            onDismissRequest = { cancelTarget = null },
            title = { Text("Отменить бронирование?") },
            text = { Text("${res.carModelName}\n${res.startDateTime.take(10)} — ${res.endDateTime.take(10)}") },
            confirmButton = {
                Button(onClick = {
                    viewModel.cancelBooking(context, res.id)
                    cancelTarget = null
                }) { Text("Да, отменить") }
            },
            dismissButton = { TextButton(onClick = { cancelTarget = null }) { Text("Назад") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { AccountCard(uiState.email, uiState.role, onAdminClick) }

            item {
                Text("Мои бронирования",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            when {
                uiState.isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.reservations.isEmpty() -> item {
                    Text("У вас пока нет бронирований",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> items(uiState.reservations) { reservation ->
                    ReservationCard(reservation, onCancel = { cancelTarget = reservation })
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.logout(context); onLogout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Выйти")
                }
            }
        }
    }
}

@Composable
private fun AccountCard(email: String, role: String, onAdminClick: () -> Unit) {
    val roleLabel = when (role) {
        "ADMIN" -> "Администратор"
        "MANAGER" -> "Менеджер"
        "CLIENT" -> "Клиент"
        else -> role
    }
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Аккаунт", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(email, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(roleLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (role == "ADMIN" || role == "MANAGER") {
                Spacer(Modifier.height(8.dp))
                Button(onClick = onAdminClick, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Панель управления")
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(reservation: ReservationDto, onCancel: () -> Unit) {
    val statusColor = CarLabels.reservationStatusColor(reservation.status)
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(reservation.carModelName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(statusColor))
                        Text(CarLabels.reservationStatus(reservation.status),
                            style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Text("${reservation.startDateTime.take(10)} → ${reservation.endDateTime.take(10)}",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("${reservation.amount.toInt()} ${reservation.currency}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary)
                Text(CarLabels.paymentStatus(reservation.status),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CarLabels.paymentStatusColor(reservation.status))
            }

            if (CarLabels.canCancel(reservation.status)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Отменить бронирование")
                }
            }
        }
    }
}
