package com.dailystudio.careermate.core.repository

import android.content.Context
import com.dailystudio.careermate.core.utils.PDFUtils
import com.dailystudio.devbricksx.development.Logger
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File

class GemmaAIRepository(
    context: Context,
    dispatcher: CoroutineDispatcher
): BaseAIRepository(context, dispatcher) {

    companion object {
        // NB: Make sure the filename is *unique* per model you use!
        // Weight caching is currently based on filename alone.
        private const val MODEL_PATH = "/data/local/tmp/llm/model.bin"
    }

    private val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    private var llmInference: LlmInference

    init {
        if (!modelExists) {
            throw IllegalArgumentException("Model not found at path: $MODEL_PATH")
        }

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(9999)
            .setResultListener { partialResult, done ->
                Logger.debug("new partial result: $done")
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    override suspend fun generateContent(
        prompt: String,
        fileUri: String?,
        mimeType: String?
    ): String? {
        var composedPrompt = if (fileUri != null && !mimeType.isNullOrBlank()) {
            if (mimeType.contains("pdf")) {
                PDFUtils.extractTextFromPdf(fileUri)
            } else {
                ""
            }
        } else {
            ""
        }

        composedPrompt += "\n $prompt"
        Logger.debug("[AI] composed prompt: $composedPrompt")

        return llmInference.generateResponse(composedPrompt)
    }


}