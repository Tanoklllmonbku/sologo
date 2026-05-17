package com.sologo.app.domain.usecase.lost.admin

import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.repository.LostRepository
import com.sologo.app.utils.Result

class GetAllLostReportsUseCase(
    private val lostRepository: LostRepository
) {
    suspend operator fun invoke(): Result<List<LostReport>> {
        return lostRepository.getAllLostReports()
    }
}