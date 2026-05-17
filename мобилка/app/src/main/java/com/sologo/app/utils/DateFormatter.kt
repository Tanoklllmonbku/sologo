// utils/DateFormatter.kt
package com.sologo.app.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Форматирует даты для отправки на сервер
 * Используется ТОЛЬКО когда мы создаём запрос (BookingRequest, RegisterRequest и т.д.)
 */
object DateFormatter {
    // Формат, который ожидает сервер в запросах
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Превращает три числа в строку "2024-01-15"
    fun toApiDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return apiFormat.format(calendar.time)
    }

    // Превращает Date в строку "2024-01-15"
    fun toApiDate(date: Date): String {
        return apiFormat.format(date)
    }

    // Превращает строку "2024-01-15" в Date (для тестов)
    fun fromApiDate(dateString: String): Date? {
        return try {
            apiFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}