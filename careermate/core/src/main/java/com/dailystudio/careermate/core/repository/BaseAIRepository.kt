package com.dailystudio.careermate.core.repository

import android.content.Context
import com.dailystudio.careermate.core.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

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

            generateContent(
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
            generateContent(prompt, fileUri, mimeType)
        }
    }

    abstract suspend fun generateContent(
        prompt: String,
        fileUri: String? = null,
        mimeType: String? = null
    ): String?

    open fun close() {}
}