package com.sologo.app.utils

object ImageUrlHelper {
    private const val BASE_URL = "http://10.0.2.2:8000"

    fun toFullImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) {
            path
        } else if (path.startsWith("/static/")) {
            "$BASE_URL$path"
        } else {
            "$BASE_URL/static/$path"
        }
    }

    // Для списка фото (room_images)
    fun toFullImageUrls(paths: List<String>?): List<String>? {
        return paths?.mapNotNull { toFullImageUrl(it) }
    }
}