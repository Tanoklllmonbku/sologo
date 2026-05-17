package com.sologo.app.models.request.user

import com.google.gson.annotations.SerializedName

data class UserUpdatePasswordRequest (
    @SerializedName("old_password")
    val oldPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)