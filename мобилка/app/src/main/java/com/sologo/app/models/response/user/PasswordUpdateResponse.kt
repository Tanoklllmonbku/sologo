package com.sologo.app.models.response.user

import com.google.gson.annotations.SerializedName

data class PasswordUpdateResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("success")
    val success: Boolean = true
)