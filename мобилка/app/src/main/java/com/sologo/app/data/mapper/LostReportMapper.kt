// data/mapper/LostReportMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.model.LostStatus
import com.sologo.app.models.response.lost.LostReportResponse

object LostReportMapper {
    fun toDomain(response: LostReportResponse): LostReport {
        return LostReport(
            id = response.reportId,
            userId = response.userId,
            userNickname = response.userNickname,
            lat = response.lat,
            lng = response.lng,
            message = response.message,
            status = LostStatus.fromString(response.status),
            createdAt = response.createdAt
        )
    }
}