package com.sologo.app.domain.usecase.lost

import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.repository.LostRepository
import com.sologo.app.utils.Result

class GetMyLostReportsUseCase(
    private val lostRepository: LostRepository
) {
    suspend operator fun invoke(): Result<List<LostReport>> {
        return lostRepository.getMyLostReports()
    }
}