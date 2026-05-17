// data/repository/impl/AuthRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.UserMapper
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.models.request.auth.LoginRequest
import com.sologo.app.models.request.auth.RegisterRequest
import com.sologo.app.network.TokenManager
import com.sologo.app.network.api.UserApi
import com.sologo.app.utils.Result
import java.io.IOException

class AuthRepositoryImpl(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : AuthRepository {


    override suspend fun register(
        nickname: String,
        email: String,
        password: String,
        phoneNumber: String?
    ): Result<User> {
        return try {
            val request = RegisterRequest(nickname = nickname, email=email, password = password, phoneNumber=phoneNumber)
            val response = userApi.register(request)
            Result.Success(UserMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка регистрации: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val request = LoginRequest(email, password)
            val response = userApi.login(request)
            tokenManager.saveToken(response.accessToken)
            Result.Success(response.accessToken)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка входа: ${e.message ?: "Неверный email или пароль"}")
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }

    override fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
}