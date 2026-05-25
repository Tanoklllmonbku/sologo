package com.sologo.app.utils.validators

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateValidator {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
    }

    /**
     * Проверяет, является ли строка валидной датой в формате yyyy-MM-dd
     */
    fun isValid(dateStr: String): Boolean {
        return try {
            dateFormat.parse(dateStr)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Проверяет, что дата сегодня или в будущем (исправлено)
     */
    fun isFutureOrToday(dateStr: String): Boolean {
        val date = parseDate(dateStr) ?: return false
        val today = Date()
        return date.after(today) || isSameDay(date, today)
    }

    /**
     * Проверяет, что дата в будущем (строго, без сегодня)
     */
    fun isFuture(dateStr: String): Boolean {
        val date = parseDate(dateStr) ?: return false
        val today = Date()
        return date.after(today) && !isSameDay(date, today)
    }

    /**
     * Проверяет, что дата в прошлом
     */
    fun isPast(dateStr: String): Boolean {
        val date = parseDate(dateStr) ?: return false
        val today = Date()
        return date.before(today) && !isSameDay(date, today)
    }

    /**
     * Проверяет, что дата после другой даты (или равна? зависит от needStrict)
     */
    fun isAfter(dateStr: String, afterDateStr: String, needStrict: Boolean = true): Boolean {
        val date = parseDate(dateStr) ?: return false
        val afterDate = parseDate(afterDateStr) ?: return false
        return if (needStrict) date.after(afterDate) else !date.before(afterDate)
    }

    /**
     * Проверяет, что дата до другой даты
     */
    fun isBefore(dateStr: String, beforeDateStr: String): Boolean {
        val date = parseDate(dateStr) ?: return false
        val beforeDate = parseDate(beforeDateStr) ?: return false
        return date.before(beforeDate)
    }

    /**
     * Количество дней между двумя датами
     */
    fun daysBetween(startDateStr: String, endDateStr: String): Int? {
        val startDate = parseDate(startDateStr) ?: return null
        val endDate = parseDate(endDateStr) ?: return null
        val diff = endDate.time - startDate.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}