package com.sologo.app.network.api

import com.sologo.app.models.request.auth.LoginRequest
import com.sologo.app.models.request.auth.RegisterRequest
import com.sologo.app.models.request.user.UserUpdatePasswordRequest
import com.sologo.app.models.request.user.UserUpdateRequest
import com.sologo.app.models.response.auth.AuthResponse
import com.sologo.app.models.response.user.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    // ========== USER ==========
    @POST("api/v1/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @GET("api/v1/users/me")
    suspend fun getMe(): UserResponse

    @POST("api/v1/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @PATCH("api/v1/users/me")
    suspend fun updateMe(@Body request: UserUpdateRequest): UserResponse

    @PATCH("api/v1/users/me/password")
    suspend fun updatePassword(@Body request: UserUpdatePasswordRequest): UserResponse

    // ========== ADMIN ==========
    @GET("api/v1/users/")
    suspend fun getAllUsers(): List<UserResponse>

    @PATCH("api/v1/users/{userId}")
    suspend fun adminUpdateUser(
        @Path("userId") userId: Int,
        @Body request: UserUpdateRequest
    ): UserResponse

}