package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.SubscriptionEntitlement

interface SubscriptionRepository {
    suspend fun getEntitlement(): SubscriptionEntitlement
}
