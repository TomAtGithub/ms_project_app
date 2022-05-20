package com.example.ms_project_android

import java.io.FileReader

private const val DELIMITER = ";"
private const val LOG_TAG = "CSVHandler"

class CSVHandler {
    /**
     * Reads the csv file created by openSMILE.
     * Some values are null so I created an own implementation
     * because the readers I tested ("opencsv", "kotlin-csv") could not handle that.
     *
     */
    fun read(path: String, to: Int = 288): HashMap<String, Float> {
        val fileReader = FileReader(path)
        val csvData = fileReader.readText()
        val rows = csvData.split("\n")

        val headers = rows[0].split(DELIMITER)
        val values = rows[1].split(DELIMITER)
        val dataMap = hashMapOf<String, Float>()

        for(index in values.indices) {
            if(index >= to) {
                break
            }
            val name = headers[index]
            val value = values[index]

            if(value.isNotEmpty() and value.isNotEmpty()) {
                dataMap[name] = value.toFloat()
            }
        }
        return dataMap
    }
}