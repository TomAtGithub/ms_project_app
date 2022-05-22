package com.example.ms_project_android

import android.content.Context

private const val OPEN_SMILE_CONFIG = "config/MFCC12_0_D_A.func.conf"

class GlobalConfig private constructor() {

    companion object {
        private var instance : Config? = null

        fun init(context: Context, cacheDir: String) {
            if (instance == null) {
                instance = Config(
                    cacheDir = cacheDir,
                    recordPath = "$cacheDir/record.wav",
                    featuresCsvPath = "$cacheDir/mfcc.func.csv",
                    evaluationCsvPath = "$cacheDir/evaluation.csv",
                    openSmileConfigPath = Utils.getAsset(context, OPEN_SMILE_CONFIG).path
                )
            }
        }
        fun getInstance(): Config {
            return instance!!
        }
    }
}

data class Config(
    val cacheDir: String,
    val recordPath: String,
    val featuresCsvPath: String,
    val evaluationCsvPath: String,
    val openSmileConfigPath: String
)