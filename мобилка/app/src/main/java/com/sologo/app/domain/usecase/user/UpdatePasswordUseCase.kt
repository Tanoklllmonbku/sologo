package com.sologo.app.domain.usecase.user

import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.utils.Result

class UpdatePasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String): Result<User> {
        if (oldPassword.isBlank()) {
            return Result.Error("Старый пароль не может быть пустым")
        }
        if (newPassword.isBlank()) {
            return Result.Error("Новый пароль не может быть пустым")
        }
        if (newPassword.length < 6) {
            return Result.Error("Новый пароль должен быть минимум 6 символов")
        }
        if (oldPassword == newPassword) {
            return Result.Error("Новый пароль должен отличаться от старого")
        }
        return userRepository.updatePassword(oldPassword, newPassword)
    }
}