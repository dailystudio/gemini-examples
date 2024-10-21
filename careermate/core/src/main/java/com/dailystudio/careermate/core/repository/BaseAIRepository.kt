package com.dailystudio.careermate.core.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class BaseAIRepository(
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun generate(
        prompt: String,
        fileUri: String? = null,
        mimeType: String? = null
    ): String? {
        return withContext(dispatcher) {
            generateContent(prompt, fileUri, mimeType)
        }
    }

    abstract suspend fun generateContent(
        prompt: String,
        fileUri: String? = null,
        mimeType: String? = null
    ): String?

}