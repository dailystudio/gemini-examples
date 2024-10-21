package com.dailystudio.careermate.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dailystudio.careermate.R
import com.dailystudio.careermate.core.model.ResumeViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import com.dailystudio.devbricksx.fragment.DevBricksFragment
import kotlinx.coroutines.launch

class ChatFragment: AbsPermissionsFragment() {

    private var userInput: TextView? = null
    private var results: TextView? = null
    private var sendButton: View? = null
    private var pickButton: View? = null

    private lateinit var resumeViewModel: ResumeViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resumeViewModel = ViewModelProvider(this)[ResumeViewModel::class.java]

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                resumeViewModel.result.collect {
                    results?.text = it
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userInput = view.findViewById(R.id.user_input)
        userInput?.addTextChangedListener {

        }

        results = view.findViewById(R.id.results)

        sendButton = view.findViewById(R.id.send)
        sendButton?.setOnClickListener {
            resumeViewModel.generate(userInput?.text.toString())
        }

        pickButton = view.findViewById(R.id.pick)
        pickButton?.setOnClickListener {
            checkOrGrantPermissions()
        }

    }

    private val pickMidiFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                Logger.debug("[FILE] selected uri: $uri")

                uri?.let {
                    val mineType = requireContext().contentResolver.getType(uri)
                    Logger.debug("[FILE] mineType: $mineType")

                    resumeViewModel.generate(
                        userInput?.text.toString(),
                        it.toString(),
                        mineType
                    )
                }
            }
        }

    override val autoCheckPermissions: Boolean
        get() = false

    override fun getPermissionsPromptViewId(): Int {
        return -1
    }

    override fun getRequiredPermissions(): Array<String> {
        return arrayOf()
    }

    override fun onPermissionsDenied() {
    }

    override fun onPermissionsGranted(newlyGranted: Boolean) {
        pickFile()
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // 接受任意类型的文件
        val mimeTypes = arrayOf("application/pdf", "image/*")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        pickMidiFileLauncher.launch(intent)
    }

}