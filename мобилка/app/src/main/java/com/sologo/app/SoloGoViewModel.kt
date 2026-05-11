package com.sologo.app

import androidx.lifecycle.ViewModel
import com.sologo.app.data.Booking
import com.sologo.app.data.ChatMessage
import com.sologo.app.data.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SoloGoViewModel : ViewModel() {

    private val _nickname = MutableStateFlow("Соло-путешественник")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                author = "SoloGo",
                text = "Добро пожаловать в чат соло-путешественников. Делитесь опытом и полезными находками.",
                isFromUser = false,
            ),
        ),
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _translatorInput = MutableStateFlow("")
    val translatorInput: StateFlow<String> = _translatorInput.asStateFlow()

    fun setNickname(value: String) {
        _nickname.value = value.trim().ifBlank { "Соло-путешественник" }
    }

    fun bookHotel(title: String, checkInDate: String, checkOutDate: String) {
        _bookings.update { current ->
            val withoutCurrentHotel = current.filterNot { it.hotelName == title }
            withoutCurrentHotel + Booking(
                hotelName = title,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
            )
        }
    }

    fun removeBooking(hotelName: String) {
        _bookings.update { current -> current.filterNot { it.hotelName == hotelName } }
    }

    fun sendChat(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val name = _nickname.value
        val mine = ChatMessage(author = name, text = trimmed, isFromUser = true)
        val echo = ChatMessage(
            author = "Сообщество",
            text = "Удачной поездки! Кто-нибудь был в этом городе в одиночку?",
            isFromUser = false,
        )
        _chatMessages.update { it + mine + echo }
    }

    fun setTranslatorInput(value: String) {
        _translatorInput.value = value
    }

    fun translatePhrase(): String {
        val key = _translatorInput.value.trim().lowercase()
        if (key.isEmpty()) return "Введите фразу на русском."
        val hit = SampleData.phrasebook.entries.firstOrNull { (ru, _) ->
            ru.contains(key) || key.contains(ru)
        }
        return hit?.let { (ru, en) -> "«$ru» → $en" }
            ?: "Точного совпадения не найдено. Попробуйте: «где вокзал», «спасибо»."
    }
}
