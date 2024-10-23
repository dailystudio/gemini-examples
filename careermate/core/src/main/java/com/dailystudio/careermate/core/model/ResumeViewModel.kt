package com.dailystudio.careermate.core.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailystudio.careermate.core.repository.BaseAIRepository
import com.dailystudio.careermate.core.repository.GeminiAIRepository
import com.dailystudio.careermate.core.repository.GeminiNanoRepository
import com.dailystudio.careermate.core.repository.GemmaAIRepository
import com.dailystudio.careermate.core.repository.VertexAIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AIEngine {
    GEMINI,
    GEMINI_NANO,
    VERTEX,
    GEMMA
}

class ResumeViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private val ENGINE = AIEngine.VERTEX
    }

    private val repo: BaseAIRepository = when (ENGINE) {
        AIEngine.GEMINI -> GeminiAIRepository(application, Dispatchers.IO)
        AIEngine.GEMINI_NANO -> GeminiNanoRepository(application, Dispatchers.IO)
        AIEngine.VERTEX -> VertexAIRepository(application, Dispatchers.IO)
        AIEngine.GEMMA -> GemmaAIRepository(application, Dispatchers.IO)
    }

    private val _result: MutableStateFlow<String?> = MutableStateFlow(null)
    val result = _result.asStateFlow()

    fun analyzeResume(textOfResume: String) {
        viewModelScope.launch {
            _result.value = repo.analyzeResumeFromText(textOfResume)
        }
    }

    fun analyzeResumeDocument(filePath: String, mimeType: String) {
        viewModelScope.launch {
            _result.value = repo.analyzeResumeFromDocument(filePath, mimeType)
        }
    }

    fun generate(
        prompt: String,
        fileUri: String? = null,
        mineType: String? = null
    ) {
        viewModelScope.launch {
            _result.value = repo.generate(
                prompt,
                fileUri,
                mineType
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        repo.close()
    }

}