package com.sologo.app.models.request.user

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest (
    @SerializedName("email")
    val nickname: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null
)