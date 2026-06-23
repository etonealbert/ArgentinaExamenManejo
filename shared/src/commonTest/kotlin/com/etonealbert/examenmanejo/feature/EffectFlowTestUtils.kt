package com.etonealbert.examenmanejo.feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EffectCollector<T>(
    private val values: MutableList<T>,
    private val job: kotlinx.coroutines.Job,
) {
    fun assertSingleEffect(expected: T) {
        try {
            assertEquals(listOf(expected), values)
        } finally {
            job.cancel()
        }
    }
}

internal fun <T> collectNextEffect(flow: SharedFlow<T>): EffectCollector<T> {
    val values = mutableListOf<T>()
    val job = CoroutineScope(Dispatchers.Unconfined).launch {
        values += flow.first()
    }
    return EffectCollector(values, job)
}

internal fun <T> assertNoReplayedEffect(flow: SharedFlow<T>) {
    val values = mutableListOf<T>()
    val job = CoroutineScope(Dispatchers.Unconfined).launch {
        values += flow.first()
    }
    try {
        assertTrue(values.isEmpty(), "Expected no replayed effect for a new collector")
    } finally {
        job.cancel()
    }
}
