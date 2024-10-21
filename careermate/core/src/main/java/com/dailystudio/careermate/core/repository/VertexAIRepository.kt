package com.dailystudio.careermate.core.repository

import android.graphics.BitmapFactory
import android.net.Uri
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream

class VertexAIRepository(
    dispatcher: CoroutineDispatcher
): BaseAIRepository(dispatcher) {

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
                                GlobalContextWrapper.context?.contentResolver?.openInputStream(
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