package com.sologo.app.domain.usecase.auth

import android.util.Patterns
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.utils.Result

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository  // ← добавить зависимость
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        if (email.isBlank()) {
            return Result.Error("Email не может быть пустым")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error("Неверный формат email")
        }
        if (password.isBlank()) {
            return Result.Error("Пароль не может быть пустым")
        }
        if (password.length < 6) {
            return Result.Error("Пароль должен быть минимум 6 символов")
        }
        return authRepository.login(email, password)
    }

    // ← ДОБАВИТЬ МЕТОД
    suspend fun getCurrentUser(): Result<User> {
        return userRepository.getProfile()
    }
}