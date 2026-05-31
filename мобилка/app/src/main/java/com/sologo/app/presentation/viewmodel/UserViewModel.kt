package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.User
import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.domain.usecase.user.GetProfileUseCase
import com.sologo.app.domain.usecase.user.UpdatePasswordUseCase
import com.sologo.app.domain.usecase.user.UpdateProfileUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _profileState = MutableStateFlow<Result<User>>(Result.Idle)
    val profileState: StateFlow<Result<User>> = _profileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Result<User>>(Result.Idle)
    val updateProfileState: StateFlow<Result<User>> = _updateProfileState.asStateFlow()

    private val _updatePasswordState = MutableStateFlow<Result<User>>(Result.Idle) 
    val updatePasswordState: StateFlow<Result<User>> = _updatePasswordState.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = authRepository.isLoggedIn()
            if (_isLoggedIn.value) {
                loadProfile()
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = Result.Loading
            val result = getProfileUseCase()
            _profileState.value = result
            if (result is Result.Error) {
                _isLoggedIn.value = false
            }
        }
    }

    fun updateProfile(nickname: String?, email: String?, phoneNumber: String?) {
        viewModelScope.launch {
            _updateProfileState.value = Result.Loading
            val result = updateProfileUseCase(nickname, email, phoneNumber)
            _updateProfileState.value = result
            if (result is Result.Success) {
                loadProfile()
            }
        }
    }

    fun updatePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _updatePasswordState.value = Result.Loading
            val result = updatePasswordUseCase(oldPassword, newPassword)
            _updatePasswordState.value = result
            if (result is Result.Success) {
                loadProfile()  // ← перезагружаем профиль
            }
        }
    }

    fun clearStates() {
        _updateProfileState.value = Result.Idle
        _updatePasswordState.value = Result.Idle
    }
}