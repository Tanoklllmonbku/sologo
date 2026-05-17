// data/repository/impl/UserRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.UserMapper
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.models.request.user.UserUpdatePasswordRequest
import com.sologo.app.models.request.user.UserUpdateRequest
import com.sologo.app.network.TokenManager
import com.sologo.app.network.api.UserApi
import com.sologo.app.utils.Result
import java.io.IOException

class UserRepositoryImpl(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun getProfile(): Result<User> {
        return try {
            val response = userApi.getMe()
            Result.Success(UserMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки профиля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updateProfile(
        nickname: String?,
        email: String?,
        phoneNumber: String?
    ): Result<User> {
        return try {
            val request = UserUpdateRequest(nickname, email, phoneNumber)
            val response = userApi.updateMe(request)
            Result.Success(UserMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления профиля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updatePassword(oldPassword: String, newPassword: String): Result<User> {
        return try {
            val request = UserUpdatePasswordRequest(oldPassword, newPassword)
            val response = userApi.updatePassword(request)
            Result.Success(UserMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка смены пароля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = userApi.getAllUsers()
            Result.Success(response.map { UserMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки пользователей: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun adminUpdateUser(
        userId: Int,
        nickname: String?,
        email: String?,
        phoneNumber: String?
    ): Result<User> {
        return try {
            val request = UserUpdateRequest(nickname, email, phoneNumber)
            val response = userApi.adminUpdateUser(userId, request)
            Result.Success(UserMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления пользователя: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}