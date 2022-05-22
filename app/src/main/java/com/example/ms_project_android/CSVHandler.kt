package com.example.ms_project_android

import android.util.Log
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private const val DELIMITER = ";"
private const val LOG_TAG = "CSVHandler"
private const val N_FEATURES = 228

class CSVHandler {
    /**
     * Reads the csv file created by openSMILE.
     * Some values are null so I created an own implementation
     * because the readers I tested ("opencsv", "kotlin-csv") could not handle that.
     *
     */
    fun read(path: String): Array<FloatArray> {
        val fileReader = FileReader(path)
        val csvData = fileReader.readText()
        val rows = csvData.split("\n")

        val features = Array(rows.size-1) {FloatArray(N_FEATURES)}

        for (row_idx in 1 until rows.size) {
            val values = rows[row_idx].split(DELIMITER)

            for (index in values.indices) {
                if (index >= N_FEATURES) {
                    break
                }

                features[row_idx-1][index] = values[index].toFloat()
            }
        }

        Log.d(LOG_TAG, "read features: ${features.contentDeepToString()}")

        return features
    }

    fun write(input: Array<String>, header: Array<String>) {
        val config = GlobalConfig.getInstance()
        val file = File(config.evaluationCsvPath)
        val separator = ","

        if(!file.exists()) {
            file.writeText(header.joinToString(separator)+"\n")
        }
        file.appendText(input.joinToString(separator)+"\n")
    }

    fun deleteFile(filePath: String) {
        val file = File(filePath)
        if(file.exists()) {
            file.delete()
            Log.d(LOG_TAG, "DELETE FILE")
        }
    }
}