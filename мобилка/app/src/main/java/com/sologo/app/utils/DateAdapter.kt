// utils/DateAdapter.kt
package com.sologo.app.utils

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Превращает строку из JSON в Date объект и обратно
 * Пример: "2024-01-15T10:30:00" → Date(2024, 1, 15, 10, 30, 0)
 */
class DateAdapter : TypeAdapter<Date>() {
    // Формат, в котором сервер присылает даты
    private val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    override fun write(writer: JsonWriter, value: Date?) {
        // Когда отправляем дату на сервер
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(serverFormat.format(value))
        }
    }

    override fun read(reader: JsonReader): Date? {
        // Когда получаем дату от сервера
        val dateStr = reader.nextString()
        return try {
            serverFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}