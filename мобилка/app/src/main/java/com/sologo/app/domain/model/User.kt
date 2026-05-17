package com.sologo.app.domain.model

import java.util.Date

data class User(
    val id: Int,
    val nickname: String,
    val email: String,
    val phoneNumber: String?,
    val role: UserRole,
    val createdAt: Date
)

enum class UserRole {
    USER, ADMIN;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.lowercase()) {
                "admin" -> ADMIN
                else -> USER
            }
        }
    }
}