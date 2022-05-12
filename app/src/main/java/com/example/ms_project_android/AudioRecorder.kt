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

class AudioRecorder(context: Context, filePath: String) {
    private val filePath = filePath
//    private val d = context.getExternalFilesDir("records")
//    private val filePath = d!!.path + "/record1.wav"
//    private val filePath = context.filesDir?.path + "/$fileName"
//    private var mRecord = file
//    private val mRecord = File("test")
//    private var mWavRecorder = WavRecorder(context)
//    private var mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        MediaRecorder(context)
//    } else {
//        MediaRecorder()
//    }
//    private var isRecording = false

    private val mWaveRecorder = WaveRecorder(filePath)

    fun startRecording() {
//        mWavRecorder.startRecording(fileName, true)
        mWaveRecorder.startRecording()
        mWaveRecorder.waveConfig.sampleRate = 44100
        mWaveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        mWaveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_MONO
        Log.d(LOG_TAG, "start recording, $filePath")
    }
    fun stopRecording() {
        mWaveRecorder.stopRecording()
        Log.d(LOG_TAG, "stop recording")
    }

//    fun startRecordingOld() {
//        Log.d(LOG_TAG, "log to: ${mRecord.absoluteFile}")
//        mRecorder.reset()
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
//        mRecorder.setOutputFile(mRecord)
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
//
//        try {
//            mRecorder.prepare()
//        } catch (e: IOException) {
//            Log.e(LOG_TAG, "prepare() failed")
//        }
//
//        isRecording = true
//        mRecorder.start()
//    }
//
//    fun stopRecordingOld() {
//        mRecorder.stop()
//        mRecorder.reset()
//        isRecording = false
//    }
//
//    fun isRecording(): Boolean {
//        return isRecording
//    }
}