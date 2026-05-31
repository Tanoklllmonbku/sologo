package com.sologo.app.models.response.user

import com.google.gson.annotations.SerializedName

data class UserUpdatePasswordResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("success")
    val success: Boolean = true
)