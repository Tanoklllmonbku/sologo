package com.sologo.app.data

enum class Mood(val labelRu: String) {
    CALM("Спокойный отдых"),
    ACTIVE("Активный"),
    CULTURAL("Культурный"),
}

data class RouteIdea(
    val id: String,
    val title: String,
    val description: String,
    val mood: Mood,
    val city: String,
    val imageRes: Int,
)

data class Hotel(
    val id: String,
    val name: String,
    val city: String,
    val pricePerNight: Int,
    val avgCityPrice: Int,
    val description: String,
    val mainImageRes: Int,
    val roomImageRes: List<Int> = emptyList(),
)

data class Booking(
    val hotelName: String,
    val checkInDate: String,
    val checkOutDate: String,
)

enum class SafetyLevel(val labelRu: String) {
    HIGH("Высокая"),
    MEDIUM("Средняя"),
    LOW("Ниже среднего (осторожно)"),
}

data class SafeZone(
    val district: String,
    val city: String,
    val level: SafetyLevel,
    val note: String,
)

data class FaqItem(
    val question: String,
    val answer: String,
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val author: String,
    val text: String,
    val isFromUser: Boolean,
)
