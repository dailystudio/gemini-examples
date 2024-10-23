package com.dailystudio.careermate.core.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.dailystudio.devbricksx.development.Logger
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.CoroutineDispatcher
import java.io.InputStream

class VertexAIRepository(
    context: Context,
    dispatcher: CoroutineDispatcher
): BaseAIRepository(context, dispatcher) {

    private val model = Firebase.vertexAI.generativeModel("gemini-1.5-flash-001")

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
                                inlineData(stream.readBytes(), mimeType)
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