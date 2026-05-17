package com.sologo.app.domain.usecase.user

import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.utils.Result

class GetProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return userRepository.getProfile()
    }
}