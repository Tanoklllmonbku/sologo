package com.sologo.app.domain.usecase.user.admin

import android.util.Patterns
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.utils.Result

class AdminUpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: Int,
        nickname: String? = null,
        email: String? = null,
        phoneNumber: String? = null
    ): Result<User> {
        if (userId <= 0) {
            return Result.Error("Неверный ID пользователя")
        }
        if (email != null && email.isNotBlank()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.Error("Неверный формат email")
            }
        }
        return userRepository.adminUpdateUser(userId, nickname, email, phoneNumber)
    }
}