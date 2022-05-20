package com.example.ms_project_android

import android.content.Context
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException

private const val LOG_TAG = "AudioClassifier"

class AudioClassifierOld(context: Context) {

    private lateinit var record: AudioRecord
    private val basePath = context.cacheDir.path
    private var fileName = context.cacheDir.path + "/test"
    private var mRecord = File(fileName)
    private var mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }
    private var mPlayer = MediaPlayer()
    private var isRecordLoaded = false
    private var isRecording = false
    private var isPlaying = false

    fun startRecording() {
        Log.d(LOG_TAG, "log to: $fileName")
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
    fun isRecording(): Boolean {
        return isRecording
    }
    fun stopRecording() {
        mRecorder.stop()
        mRecorder.reset()
        isRecording = false
    }
    fun release() {
        mRecorder.release()
        mPlayer.release()
    }
    fun load(): Boolean {
        return try {
            mPlayer.reset()
            mPlayer.setDataSource(fileName)
            mPlayer.prepare()
            isRecordLoaded = true
            true
        } catch (e: IllegalAccessException) {
            // file does not exist
            isRecordLoaded = false
            false
        }
    }
    fun getStats(): RecordStats {
        return RecordStats(
            duration = mPlayer.duration,
            emotion = "Test Emotion"
        )
    }
    fun isPlaying(): Boolean {
        return isPlaying
    }
    fun play() {
        if(isRecordLoaded) {
            mPlayer.start()
        }
        isPlaying = true
    }
    fun pause() {
        mPlayer.stop()
        isPlaying = false
    }
    fun classifyTest() {
        val mfccExtractor = MFCCExtractor(39)
        val meanMfcc = mfccExtractor.getMeanMFCC(fileName)
        val fw = FileWriter("$basePath/meanMfcc.csv")
        fw.write(meanMfcc.joinToString(","))
        fw.close()
    }
}

data class RecordStats(
    val duration: Int,
    val emotion: String
): java.io.Serializable