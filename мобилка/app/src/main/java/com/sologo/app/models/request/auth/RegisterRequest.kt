package com.sologo.app.models.request.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequest (

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("password")
    val password: String
)