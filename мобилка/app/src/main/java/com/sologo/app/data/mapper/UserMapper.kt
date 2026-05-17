// data/mapper/UserMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.User
import com.sologo.app.domain.model.UserRole
import com.sologo.app.models.response.user.UserResponse
import java.util.Date

object UserMapper {
    fun toDomain(response: UserResponse): User {
        return User(
            id = response.userId,
            nickname = response.nickname,
            email = response.email,
            phoneNumber = response.phoneNumber,
            role = UserRole.fromString(response.role),
            createdAt = response.createdAt
        )
    }
}