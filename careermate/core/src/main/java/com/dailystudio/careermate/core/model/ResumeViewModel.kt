package com.dailystudio.careermate.core.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailystudio.careermate.core.repository.BaseAIRepository
import com.dailystudio.careermate.core.repository.GeminiRepository
import com.dailystudio.careermate.core.repository.VertexAIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResumeViewModel(application: Application): AndroidViewModel(application) {

    private val repo: BaseAIRepository =
        VertexAIRepository(Dispatchers.IO)
//        GeminiRepository(Dispatchers.IO)

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

}