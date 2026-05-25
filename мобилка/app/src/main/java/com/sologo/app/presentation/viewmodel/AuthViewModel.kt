package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.domain.usecase.auth.LoginUseCase
import com.sologo.app.domain.usecase.auth.LogoutUseCase
import com.sologo.app.domain.usecase.auth.RegisterUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<String>>(Result.Idle)
    val loginState: StateFlow<Result<String>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Result<com.sologo.app.domain.model.User>>(Result.Idle)
    val registerState: StateFlow<Result<com.sologo.app.domain.model.User>> = _registerState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            val result = loginUseCase(email, password)
            _loginState.value = result
            if (result is Result.Success) {
                _isLoggedIn.value = authRepository.isLoggedIn()  // ← ОБНОВЛЯЕМ
            }
        }
    }

    fun register(nickname: String, email: String, password: String, phoneNumber: String?) {
        viewModelScope.launch {
            _registerState.value = Result.Loading
            val result = registerUseCase(nickname, email, password, phoneNumber)
            _registerState.value = result
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _isLoggedIn.value = false  // ← ОБНОВЛЯЕМ ПОСЛЕ ВЫХОДА
        }
    }

    fun clearStates() {
        _loginState.value = Result.Idle
        _registerState.value = Result.Idle
    }

    fun checkAuthStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }
}