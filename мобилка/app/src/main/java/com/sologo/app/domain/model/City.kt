package com.sologo.app.domain.model

import java.util.Date

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val createdAt: Date
)