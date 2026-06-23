package com.etonealbert.examenmanejo.network

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class NoOpQuestionPackApiTest {
    @Test
    fun fetchManifestReturnsSuccess() = runBlocking {
        val api = NoOpQuestionPackApi()

        val result = api.fetchManifest()

        assertTrue(result.isSuccess)
    }
}
