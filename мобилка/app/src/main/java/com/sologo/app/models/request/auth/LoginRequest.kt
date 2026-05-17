package com.sologo.app.models.request.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)