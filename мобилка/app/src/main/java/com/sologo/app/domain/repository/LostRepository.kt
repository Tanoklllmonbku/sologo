package com.sologo.app.domain.repository

import com.sologo.app.domain.model.LostReport
import com.sologo.app.utils.Result

interface LostRepository {
    suspend fun reportLost(lat: Double, lng: Double, message: String?): Result<LostReport>
    suspend fun getMyLostReports(): Result<List<LostReport>>
    suspend fun getAllLostReports(): Result<List<LostReport>>
    suspend fun updateLostReportStatus(reportId: Int, status: String): Result<LostReport>
}