package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.UserStats

interface StatsRepository {
    suspend fun getUserStats(): UserStats
}
