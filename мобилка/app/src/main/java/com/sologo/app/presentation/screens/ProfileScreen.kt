// presentation/screens/ProfileScreen.kt
package com.sologo.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.SoloOffWhite
import com.sologo.app.presentation.theme.SoloWhite
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.UserViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val profileState by userViewModel.profileState.collectAsStateWithLifecycle()
    val updateProfileState by userViewModel.updateProfileState.collectAsStateWithLifecycle()
    val updatePasswordState by userViewModel.updatePasswordState.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showUpdateSuccess by remember { mutableStateOf(false) }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var showPasswordSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadProfile()
    }

    // Обновляем поля когда профиль загружен
    LaunchedEffect(profileState) {
        if (profileState is Result.Success) {
            val user = (profileState as Result.Success).data
            nickname = user.nickname
            email = user.email
            phoneNumber = user.phoneNumber ?: ""
        }
    }

    // Обработка успешного обновления профиля
    LaunchedEffect(updateProfileState) {
        if (updateProfileState is Result.Success) {
            showUpdateSuccess = true
            isEditing = false
            userViewModel.loadProfile() // Перезагружаем профиль
            // Скрываем сообщение через 3 секунды
            kotlinx.coroutines.delay(3000)
            showUpdateSuccess = false
            userViewModel.clearStates()
        }
    }

    // Обработка успешной смены пароля
    LaunchedEffect(updatePasswordState) {
        if (updatePasswordState is Result.Success) {
            showPasswordSuccess = true
            showPasswordDialog = false
            oldPassword = ""
            newPassword = ""
            kotlinx.coroutines.delay(3000)
            showPasswordSuccess = false
            userViewModel.clearStates()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = SoloOffWhite
    ) { padding ->
        when (profileState) {
            is Result.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = SoloGreen)
                }
            }

            is Result.Success -> {
                val user = (profileState as Result.Success).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    // Сообщение об успехе
                    if (showUpdateSuccess) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SoloGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Профиль успешно обновлён!",
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (showPasswordSuccess) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SoloGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Пароль успешно изменён!",
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (!isEditing) {
                        // Просмотр профиля
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SoloWhite),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = user.nickname,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = SoloGreen
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                ProfileRow(Icons.Default.Email, "Email", user.email)
                                ProfileRow(Icons.Default.Phone, "Телефон", user.phoneNumber ?: "Не указан")
                                ProfileRow(Icons.Default.Person, "Роль", if (user.role.name == "ADMIN") "Администратор" else "Пользователь")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Редактировать профиль", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showPasswordDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SoloGreen.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Сменить пароль", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Выйти", color = Color.White)
                        }
                    } else {
                        // Редактирование профиля
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SoloWhite),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Редактирование профиля",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = SoloGreen
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Ошибка при обновлении
                                if (updateProfileState is Result.Error) {
                                    Text(
                                        text = (updateProfileState as Result.Error).message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                OutlinedTextField(
                                    value = nickname,
                                    onValueChange = { nickname = it },
                                    label = { Text("Никнейм") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text("Телефон") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        userViewModel.updateProfile(
                                            nickname.takeIf { it != user.nickname },
                                            email.takeIf { it != user.email },
                                            phoneNumber.takeIf { it.isNotBlank() && it != user.phoneNumber }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = updateProfileState !is Result.Loading
                                ) {
                                    if (updateProfileState is Result.Loading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Сохранить", color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        isEditing = false
                                        userViewModel.clearStates()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Отмена")
                                }
                            }
                        }
                    }
                }
            }

            is Result.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = (profileState as Result.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { userViewModel.loadProfile() },
                        colors = ButtonDefaults.buttonColors(containerColor = SoloGreen)
                    ) {
                        Text("Повторить")
                    }
                }
            }

            else -> {}
        }
    }

    if (showPasswordDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false
                userViewModel.clearStates()
            },
            title = { Text("Смена пароля") },
            text = {
                Column {
                    if (updatePasswordState is Result.Error) {
                        Text(
                            text = (updatePasswordState as Result.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Старый пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = updatePasswordState !is Result.Loading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Новый пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = updatePasswordState !is Result.Loading
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.updatePassword(oldPassword, newPassword)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                    enabled = updatePasswordState !is Result.Loading && oldPassword.isNotBlank() && newPassword.isNotBlank()
                ) {
                    if (updatePasswordState is Result.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Сменить")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordDialog = false
                    userViewModel.clearStates()
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = SoloGreen)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}