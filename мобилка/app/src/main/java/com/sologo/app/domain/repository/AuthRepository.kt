package com.sologo.app.domain.repository

import com.sologo.app.domain.model.User
import com.sologo.app.utils.Result

interface AuthRepository {
    suspend fun register(
        nickname: String,
        email: String,
        password: String,
        phoneNumber: String?
    ): Result<User>

    suspend fun login(email: String, password: String): Result<String>

    suspend fun logout()

    fun isLoggedIn(): Boolean
}