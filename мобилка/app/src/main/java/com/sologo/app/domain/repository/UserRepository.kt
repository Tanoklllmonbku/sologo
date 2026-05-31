package com.sologo.app.domain.repository

import com.sologo.app.domain.model.User
import com.sologo.app.utils.Result

interface UserRepository {
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(nickname: String?, email: String?, phoneNumber: String?): Result<User>
    suspend fun updatePassword(oldPassword: String, newPassword: String): Result<User>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun adminUpdateUser(userId: Int, nickname: String?, email: String?, phoneNumber: String?): Result<User>
}