package com.dailystudio.careermate.core.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.dailystudio.careermate.core.utils.PDFUtils
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.content
import com.google.ai.edge.aicore.generationConfig
import kotlinx.coroutines.CoroutineDispatcher
import java.io.InputStream

class GeminiNanoRepository(
    context: Context,
    dispatcher: CoroutineDispatcher
): BaseAIRepository(context, dispatcher) {

    private val model = GenerativeModel(
        generationConfig {
            this.context = context
            temperature = 0.2f
            topK = 16
            maxOutputTokens = 2048
        }
    )

    override suspend fun generateContent(
        prompt: String,
        fileUri: String?,
        mimeType: String?
    ): String? {
        return model.generateContent(
            content {
                var composedPrompt = if (fileUri != null && !mimeType.isNullOrBlank()) {
                    if (mimeType.contains("pdf")) {
                        PDFUtils.extractTextFromPdf(fileUri)
                    } else {
                        ""
                    }
                } else {
                    ""
                } ?: ""

                composedPrompt += "\n $prompt"
                Logger.debug("[AI] composed prompt: $composedPrompt")

                text(composedPrompt)
            }
        ).text
    }

    override fun close() {
        super.close()

        model.close()
    }
}