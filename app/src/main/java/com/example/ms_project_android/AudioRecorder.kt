package com.example.ms_project_android

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

private const val LOG_TAG = "AudioRecorder"

class AudioRecorder(context: Context, file: File) {
    private var mRecord = file
    private var mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }
    private var isRecording = false

    fun startRecording() {
        Log.d(LOG_TAG, "log to: ${mRecord.absoluteFile}")
        mRecorder.reset()
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder.setOutputFile(mRecord)
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mRecorder.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

        isRecording = true
        mRecorder.start()
    }

    fun stopRecording() {
        mRecorder.stop()
        mRecorder.reset()
        isRecording = false
    }

    fun isRecording(): Boolean {
        return isRecording
    }
}