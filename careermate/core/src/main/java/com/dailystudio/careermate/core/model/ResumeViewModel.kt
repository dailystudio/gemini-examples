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

class ResumeViewModel(application: Application): AndroidViewModel(application) {

    private val repo: BaseAIRepository =
        GeminiNanoRepository(Dispatchers.IO)
//        GemmaAIRepository(Dispatchers.IO)
//        VertexAIRepository(Dispatchers.IO)
//        GeminiAIRepository(Dispatchers.IO)

    private val _result: MutableStateFlow<String?> = MutableStateFlow(null)
    val result = _result.asStateFlow()

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