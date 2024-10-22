package com.dailystudio.careermate.core.repository

import android.net.Uri
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import java.io.IOException
import java.io.InputStream

class GemmaAIRepository(
    dispatcher: CoroutineDispatcher
): BaseAIRepository(dispatcher) {

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

        llmInference = LlmInference.createFromOptions(GlobalContextWrapper.context, options)

        PDFBoxResourceLoader.init(GlobalContextWrapper.context)
    }

    override suspend fun generateContent(
        prompt: String,
        fileUri: String?,
        mimeType: String?
    ): String? {
        var composedPrompt = if (fileUri != null && !mimeType.isNullOrBlank()) {
            if (mimeType.contains("pdf")) {
                extractTextFromPdf(fileUri)
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

    private fun extractTextFromPdf(pdfPath: String): String? {
        // Using the `PdfDocument` class
        var stream: InputStream? = null
        var pdfDocument: PDDocument? = null

        return try {
            stream =
                GlobalContextWrapper.context?.contentResolver?.openInputStream(
                    Uri.parse(pdfPath)
                )

            stream?.let {
                pdfDocument = PDDocument.load(it)
                val pdfStripper = PDFTextStripper()

                pdfStripper.getText(pdfDocument)
            }
        } catch (e: IOException) {
            Logger.error("failed to extract text from pdf [${pdfPath}]: $e")
            null
        } finally {
            stream?.close()
        }
    }
}