package com.example.ms_project_android

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.File

class RecordingFragment : Fragment() {
    private lateinit var mAudioRecorder: AudioRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this.activity
        if(context != null) {
            val fileName = context.cacheDir.path + "/record"
            mAudioRecorder = AudioRecorder(context, File(fileName))
            mAudioRecorder.startRecording()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(mAudioRecorder != null) {
            mAudioRecorder.stopRecording()
        }
    }
}