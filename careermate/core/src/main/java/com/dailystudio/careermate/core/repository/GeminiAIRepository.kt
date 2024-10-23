package com.dailystudio.careermate.core.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.dailystudio.careermate.core.BuildConfig
import com.dailystudio.devbricksx.development.Logger
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineDispatcher
import java.io.InputStream

class GeminiAIRepository(
    context: Context,
    dispatcher: CoroutineDispatcher
): BaseAIRepository(context, dispatcher) {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.15f
            topK = 32
            topP = 1f
            maxOutputTokens = 4096
        },
    )

    override suspend fun generateContent(
        prompt: String,
        fileUri: String?,
        mimeType: String?
    ): String? {
        return model.generateContent(
            content {
                if (fileUri != null && !mimeType.isNullOrBlank()) {
                    if (mimeType.contains("image")) {
                        val bitmap = BitmapFactory.decodeFile(fileUri)
                        image(bitmap)
                    } else {
                        var stream: InputStream? = null
                        try {
                            stream =
                                context.contentResolver.openInputStream(
                                    Uri.parse(fileUri)
                                )

                            stream?.let {
                                blob(mimeType, stream.readBytes())
                            }

                        } catch (e: Exception) {
                            Logger.error("generate failed: ${e.message}")

                        } finally {
                            stream?.close()
                        }
                    }
                }
                text(prompt)
            }
        ).text
    }

}