package com.example.ms_project_android

import android.content.Context
import android.util.Log
import com.audeering.opensmile.OpenSmileAdapter
import com.audeering.opensmile.smileres_t

private const val OPEN_SMILE_CONFIG = "config/MFCC12_0_D_A.func.conf"
private const val LOG_TAG = "FeatureExtractor"

class FeatureExtractor(context: Context) {

    private val configPath = Utils.getAsset(context, OPEN_SMILE_CONFIG).absolutePath
    //private val outCsvPath = "${context.externalCacheDir?.path!!}/test.csv"
    private val outCsvPath = "${context.externalCacheDir?.path!!}/test.csv"

    fun run(audioPath: String): Boolean {

        val params = hashMapOf<String, String?>(
            "-I" to audioPath,
            "-O" to outCsvPath,
        )
        val loglevel = 3
        val debug = 1
        val consoleOutput = 1

        val ose = OpenSmileAdapter()
        val initState = ose.smile_initialize(configPath, HashMap(params), loglevel, debug, consoleOutput)

        Log.d(LOG_TAG, "STATE: $initState")

        val runState = ose.smile_run()

        Log.d(LOG_TAG, "STATE: $runState")

        return runState == smileres_t.SMILE_SUCCESS
    }

    fun getFeatures(): Array<FloatArray> {
        val reader = CSVHandler()
        return reader.read(outCsvPath)
    }
}