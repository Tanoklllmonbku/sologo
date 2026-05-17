package com.sologo.app.domain.usecase.auth

import android.util.Patterns
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.utils.Result

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nickname: String,
        email: String,
        password: String,
        phoneNumber: String?
    ): Result<User> {
        if (nickname.isBlank()) {
            return Result.Error("Имя пользователя не может быть пустым")
        }
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
        return authRepository.register(nickname, email, password, phoneNumber)
    }
}