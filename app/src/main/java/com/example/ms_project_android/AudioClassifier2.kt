package com.example.ms_project_android

class AudioClassifier2 {
    private val mfccExtractor = MFCCExtractor(40)

    fun getMfcc(file_path: String): FloatArray? {
        return mfccExtractor.getMeanMFCC(file_path)
    }
}