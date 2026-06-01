// presentation/viewmodel/admin/AdminUserViewModel.kt
package com.sologo.app.presentation.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.User
import com.sologo.app.domain.usecase.user.admin.AdminUpdateUserUseCase
import com.sologo.app.domain.usecase.user.admin.GetAllUsersUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminUserViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val updateUserUseCase: AdminUpdateUserUseCase
) : ViewModel() {

    private val _usersState = MutableStateFlow<Result<List<User>>>(Result.Idle)
    val usersState: StateFlow<Result<List<User>>> = _usersState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = Result.Loading
            val result = getAllUsersUseCase()
            _usersState.value = result
        }
    }

    fun updateUser(userId: Int, nickname: String?, email: String?, phoneNumber: String?) {
        viewModelScope.launch {
            val result = updateUserUseCase(userId, nickname, email, phoneNumber)
            if (result is Result.Success) {
                loadUsers()
            }
        }
    }
}