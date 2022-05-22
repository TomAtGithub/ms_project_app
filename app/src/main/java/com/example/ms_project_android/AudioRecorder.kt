package com.example.ms_project_android

import android.content.Context
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File
import java.io.IOException

private const val LOG_TAG = "AudioRecorder"

class AudioRecorder(filePath: String) {
    private val filePath = filePath
    private val mWaveRecorder = WaveRecorder(filePath)
    private var isRecording = false
    private var mDurationStart: Long = 0
    private var mDuration: Long = 0

    fun startRecording() {
        mWaveRecorder.startRecording()
        mWaveRecorder.waveConfig.sampleRate = 44100
        mWaveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        mWaveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_MONO
        isRecording = true
        mDurationStart = System.currentTimeMillis()
        Log.d(LOG_TAG, "start recording, $filePath")
    }
    fun stopRecording() {
        mWaveRecorder.stopRecording()
        isRecording = false
        setDuration()
        Log.d(LOG_TAG, "stop recording")
    }
    fun isRecording(): Boolean {
        return isRecording
    }
    fun getDuration(): Long {
        if(isRecording) {
            setDuration()
        }
        return mDuration
    }
    private fun setDuration() {
        mDuration = System.currentTimeMillis() - mDurationStart
    }
}