package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.data.local.ExamLocalDataSource
import com.etonealbert.examenmanejo.domain.model.UserStats
import com.etonealbert.examenmanejo.domain.repository.StatsRepository

class StatsRepositoryImpl(
    private val examLocalDataSource: ExamLocalDataSource,
) : StatsRepository {
    override suspend fun getUserStats(): UserStats {
        val history = examLocalDataSource.getExamHistory()
        val averageScore = if (history.isEmpty()) {
            0
        } else {
            history.sumOf { it.scorePercentage } / history.size
        }

        return UserStats(
            completedExamCount = history.size,
            averageScorePercentage = averageScore,
        )
    }
}
