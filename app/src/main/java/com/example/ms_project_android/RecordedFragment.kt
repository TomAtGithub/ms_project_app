package com.example.ms_project_android

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.lang.Error
import kotlin.experimental.and
import kotlin.math.log

private const val LOG_TAG = "RecordedFragment"

private fun getLE(buffer: ByteArray, pos: Int, numBytes: Int): Long {
    var pos = pos
    var numBytes = numBytes
    numBytes--
    pos += numBytes
    var `val`: Long = (buffer[pos] and 0xFF.toByte()).toLong()
    for (b in 0 until numBytes) `val` = (`val` shl 8) + (buffer[--pos] and 0xFF.toByte())
    return `val`
}

class RecordedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recorded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(LOG_TAG,"VIEW CREATED")



//        val btn = view.findViewById<Button>(R.id.button2)
//        val mainActivity = activity as RecordFragmentInterface
//
//        btn.setOnClickListener { view ->
//            val isPlaying = mainActivity.togglePlayback()
//            btn.text = when(isPlaying) {
//                true -> "PAUSE"
//                false -> "PLAY"
//            }
//        }


    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "ON STARTED")
        val audioClassifier = AudioClassifier2()
        val context = this.activity
        if(context != null) {
            val filePath = context.externalCacheDir?.path + "/record7.wav"
//            val filePath = context.filesDir?.path + "/record5.wav"
//            val file = File(filePath)
//            val buffer = file.readBytes()
//            val chunkId = getLE(buffer, 0, 4)
//            val origChunkId : Long = 1179011410
//            val isEq = chunkId == origChunkId
//            Log.d(LOG_TAG, "AudioFile: ${file.name}, ${file.extension}, ${file.exists()}, ${file.length()}, $chunkId $origChunkId $isEq")
            val mfcc = audioClassifier.getMfcc(filePath)

//            Log.d(LOG_TAG, "mfcc ${mfcc?.get(0)}")
//            infoView.text = mfcc.toString()
        } else {
//            infoView.text = "Error"
        }
    }
}