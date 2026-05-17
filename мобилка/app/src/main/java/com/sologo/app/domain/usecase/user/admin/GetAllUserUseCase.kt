package com.sologo.app.domain.usecase.user.admin

import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.UserRepository
import com.sologo.app.utils.Result

class GetAllUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return userRepository.getAllUsers()
    }
}