package com.sologo.app.domain.usecase.user

import android.util.Patterns
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.utils.Result

class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        nickname: String? = null,
        email: String? = null,
        phoneNumber: String? = null
    ): Result<User> {
        if (email != null && email.isNotBlank()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.Error("Неверный формат email")
            }
        }
        if (nickname != null && nickname.isBlank()) {
            return Result.Error("Имя пользователя не может быть пустым")
        }
        return userRepository.updateProfile(nickname, email, phoneNumber)
    }
}