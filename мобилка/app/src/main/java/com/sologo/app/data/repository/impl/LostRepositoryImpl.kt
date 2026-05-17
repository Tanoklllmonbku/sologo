// data/repository/impl/LostRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.LostReportMapper
import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.repository.LostRepository
import com.sologo.app.models.request.lost.LostReportRequest
import com.sologo.app.models.request.lost.LostReportStatusUpdateRequest
import com.sologo.app.network.api.BookingApi
import com.sologo.app.network.api.LostApi
import com.sologo.app.utils.Result
import java.io.IOException

class LostRepositoryImpl(
    private val lostApi: LostApi
) : LostRepository {


    override suspend fun reportLost(lat: Double, lng: Double, message: String?): Result<LostReport> {
        return try {
            val request = LostReportRequest(lat, lng, message)
            val response = lostApi.reportLost(request)
            Result.Success(LostReportMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка отправки сообщения: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getMyLostReports(): Result<List<LostReport>> {
        return try {
            val response = lostApi.getMyLostReports()
            Result.Success(response.map { LostReportMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки сообщений: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getAllLostReports(): Result<List<LostReport>> {
        return try {
            val response = lostApi.getAllLostReports()
            Result.Success(response.map { LostReportMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки сообщений: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updateLostReportStatus(reportId: Int, status: String): Result<LostReport> {
        return try {
            val request = LostReportStatusUpdateRequest(status)
            val response = lostApi.updateLostReportStatus(reportId, request)
            Result.Success(LostReportMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления статуса: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}