package com.example.ms_project_android

import android.content.Context

object Config {
    val RECORD_PATH = "records/record.wav"
    fun getRecordPath(context: Context): String {
        return "${context.cacheDir.path}/record.wav"
    }
}