package com.sologo.app.network.api

import com.sologo.app.models.request.lost.LostReportRequest
import com.sologo.app.models.request.lost.LostReportStatusUpdateRequest
import com.sologo.app.models.response.lost.LostReportResponse
import retrofit2.http.*

interface LostApi {
    // User endpoints
    @POST("lost/report")
    suspend fun reportLost(@Body request: LostReportRequest): LostReportResponse

    @GET("lost/my")
    suspend fun getMyLostReports(): List<LostReportResponse>

    // Admin endpoints
    @GET("lost/admin/all")
    suspend fun getAllLostReports(): List<LostReportResponse>

    @PATCH("lost/admin/{reportId}/status")
    suspend fun updateLostReportStatus(
        @Path("reportId") reportId: Int,
        @Body request: LostReportStatusUpdateRequest
    ): LostReportResponse
}