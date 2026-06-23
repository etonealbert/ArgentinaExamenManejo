package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.UserStats
import com.etonealbert.examenmanejo.domain.repository.StatsRepository

class GetUserStatsUseCase(
    private val statsRepository: StatsRepository,
) {
    suspend operator fun invoke(): UserStats = statsRepository.getUserStats()
}
