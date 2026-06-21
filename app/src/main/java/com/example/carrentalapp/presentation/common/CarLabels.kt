package com.example.carrentalapp.presentation.common

import androidx.compose.ui.graphics.Color

/** Единые человекочитаемые подписи и цвета для классов, статусов машин и броней. */
object CarLabels {

    val carClasses = listOf(
        "ECONOMY" to "Эконом",
        "COMFORT" to "Комфорт",
        "BUSINESS" to "Бизнес",
        "PREMIUM" to "Премиум",
        "SUV" to "Внедорожник"
    )

    val carStatuses = listOf("AVAILABLE", "RESERVED", "RENTED", "MAINTENANCE")

    fun carClass(code: String): String =
        carClasses.firstOrNull { it.first == code }?.second ?: code

    fun carStatus(code: String): String = when (code) {
        "AVAILABLE"   -> "Свободен"
        "RESERVED"    -> "Забронирован"
        "RENTED"      -> "В аренде"
        "MAINTENANCE" -> "Обслуживание"
        else          -> code
    }

    fun carStatusColor(code: String): Color = when (code) {
        "AVAILABLE"   -> Color(0xFF2E7D32)
        "RESERVED"    -> Color(0xFFE65100)
        "RENTED"      -> Color(0xFF1565C0)
        "MAINTENANCE" -> Color(0xFFB3261E)
        else          -> Color(0xFF757575)
    }

    fun reservationStatus(code: String): String = when (code) {
        "PENDING"   -> "Ожидает оплаты"
        "CONFIRMED" -> "Подтверждено"
        "ACTIVE"    -> "Активно"
        "CANCELLED" -> "Отменено"
        "COMPLETED" -> "Завершено"
        else        -> code
    }

    fun reservationStatusColor(code: String): Color = when (code) {
        "CONFIRMED", "ACTIVE" -> Color(0xFF2E7D32)
        "PENDING"             -> Color(0xFFE65100)
        "CANCELLED"           -> Color(0xFFB3261E)
        "COMPLETED"           -> Color(0xFF757575)
        else                  -> Color(0xFF1565C0)
    }

    /** Бронь можно отменить, пока она не завершена и не отменена. */
    fun canCancel(code: String): Boolean = code == "PENDING" || code == "CONFIRMED" || code == "ACTIVE"

    /** Статус оплаты, производный от статуса брони. */
    fun paymentStatus(reservationCode: String): String = when (reservationCode) {
        "PENDING"               -> "Ожидает оплаты"
        "CONFIRMED", "ACTIVE"   -> "Оплачено"
        "COMPLETED"             -> "Оплачено"
        "CANCELLED"             -> "Средства возвращены"
        else                    -> "—"
    }

    fun paymentStatusColor(reservationCode: String): Color = when (reservationCode) {
        "CONFIRMED", "ACTIVE", "COMPLETED" -> Color(0xFF2E7D32)
        "PENDING"                          -> Color(0xFFE65100)
        "CANCELLED"                        -> Color(0xFF757575)
        else                               -> Color(0xFF757575)
    }
}
