package com.dailystudio.careermate.core.repository

import android.content.Context
import com.dailystudio.careermate.core.R
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


class InferenceStats {

    var lastInferenceTime: Long = 0
    var countOfInferences: Long = 0
    var avgInferenceTime: Long = 0

    private var startTime: Long = 0

    fun markStart() {
        startTime = System.currentTimeMillis()
    }

    fun markEnd() {
        lastInferenceTime = System.currentTimeMillis() - startTime
        avgInferenceTime =
            (avgInferenceTime * countOfInferences + lastInferenceTime) / (countOfInferences + 1)

        countOfInferences++

        Logger.debug("[AI STATS]: inference time: $lastInferenceTime, avg: $avgInferenceTime")
    }
}

abstract class BaseAIRepository(
    protected val context: Context,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val JSON_SCHEMA_RESUME = """
            {
              "type": "object",
              "properties": {
                "Name": {
                  "type": "string"
                },
                "Job Title": {
                  "type": "string"
                },
                "Education": {
                  "type": "string"
                },
                "Primary Projects": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "Name": {
                        "type": "string"
                      },
                      "Time Period": {
                        "type": "string"
                      },
                      "Main Techs": {
                        "type": "array",
                        "items": {
                          "type": "string"
                        }
                      },
                      "Brief": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "Name",
                      "Time Period",
                      "Main Techs",
                      "Brief"
                    ]
                  }
                },
                "Work Experience": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "Start": {
                        "type": "string"
                      },
                      "End": {
                        "type": "string"
                      },
                      "Name of Company": {
                        "type": "string"
                      }
                    }
                  }
                }
              }
            }
        """
    }

    private val _stats: MutableStateFlow<InferenceStats> = MutableStateFlow(InferenceStats())
    val stats: StateFlow<InferenceStats> = _stats.asStateFlow()

    suspend fun analyzeResumeFromText(textOfResume: String): String? {
        return withContext(dispatcher) {
            val templateOfPrompt = context.getString(
                R.string.prompt_analyze_resume_text,
                textOfResume,
                JSON_SCHEMA_RESUME
            )

            generateContent(templateOfPrompt)
        }
    }


    suspend fun analyzeResumeFromDocument(fileUri: String, mimeType: String): String? {
        return withContext(dispatcher) {
            val templateOfPrompt = context.getString(
                R.string.prompt_analyze_resume_document,
                JSON_SCHEMA_RESUME
            )

            generate(
                templateOfPrompt,
                fileUri,
                mimeType
            )
        }
    }

    suspend fun generate(
        prompt: String,
        fileUri: String? = null,
        mimeType: String? = null
    ): String? {
        return withContext(dispatcher) {
            _stats.value.markStart()
            val ret =  generateContent(prompt, fileUri, mimeType)
            _stats.value.markEnd()

            ret
        }
    }

    abstract suspend fun generateContent(
        prompt: String,
        fileUri: String? = null,
        mimeType: String? = null
    ): String?

    open fun close() {}
}