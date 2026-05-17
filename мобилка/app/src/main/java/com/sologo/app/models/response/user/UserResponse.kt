package com.sologo.app.models.response.user

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserResponse(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("role")
    val role: String,

    @SerializedName("created_at")
    val createdAt: Date
)