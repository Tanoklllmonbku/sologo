// presentation/screens/admin/AdminUsersScreen.kt
package com.sologo.app.presentation.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.domain.model.User
import com.sologo.app.domain.model.UserRole
import com.sologo.app.presentation.admin.viewmodel.AdminUserViewModel
import com.sologo.app.presentation.screens.admin.components.AdminTopBar
import com.sologo.app.utils.Result
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    viewModel: AdminUserViewModel,
    onNavigateBack: () -> Unit
) {
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", LocalLocale.current.platformLocale)

    var editingUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Управление пользователями",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { viewModel.loadUsers() }) {
                        Icon(Icons.Default.Refresh, "Обновить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (usersState) {
                is Result.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Result.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text((usersState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadUsers() }) { Text("Повторить") }
                    }
                }
                is Result.Success -> {
                    val users = (usersState as Result.Success).data
                    if (users.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.People, null, modifier = Modifier.size(64.dp))
                            Text("Нет пользователей")
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(users) { user ->
                                UserCard(
                                    user = user,
                                    onEdit = { editingUser = user },
                                    dateFormat = dateFormat
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    editingUser?.let { user ->
        UserEditDialog(
            user = user,
            onDismiss = { editingUser = null },
            onUpdate = { nickname, email, phoneNumber ->
                viewModel.updateUser(user.id, nickname, email, phoneNumber)
                editingUser = null
            }
        )
    }
}

@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    dateFormat: SimpleDateFormat
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.nickname, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (user.role == UserRole.ADMIN) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("ADMIN")
                        }
                    } else {
                        Badge {
                            Text("USER")
                        }
                    }
                }
                Text(user.email, style = MaterialTheme.typography.bodySmall)
                user.phoneNumber?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                Text("Создан: ${dateFormat.format(user.createdAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Редактировать", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun UserEditDialog(
    user: User,
    onDismiss: () -> Unit,
    onUpdate: (nickname: String, email: String, phoneNumber: String?) -> Unit
) {
    var nickname by remember { mutableStateOf(user.nickname) }
    var email by remember { mutableStateOf(user.email) }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать пользователя") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Никнейм") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nickname.isNotBlank() && email.isNotBlank()) {
                        onUpdate(nickname, email, phoneNumber.ifEmpty { null })
                    }
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}