package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.domain.model.SubscriptionEntitlement
import com.etonealbert.examenmanejo.domain.repository.SubscriptionRepository

class FakeSubscriptionRepository : SubscriptionRepository {
    override suspend fun getEntitlement(): SubscriptionEntitlement = SubscriptionEntitlement(
        hasPremiumAccess = false,
        source = "MVP_FAKE",
    )
}
