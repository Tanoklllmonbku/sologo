package com.sologo.app.domain.usecase.auth

import com.sologo.app.domain.repository.AuthRepository

class IsLoggedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isLoggedIn()
    }
}