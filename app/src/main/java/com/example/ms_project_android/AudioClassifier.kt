/**
 * Uses code of: https://bitbucket.org/pbdfrita/msfrita_voice_recognition.git (1)
 */

package com.example.ms_project_android

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


private const val LOG_TAG = "AudioClassifier"
//private const val MODEL_FILENAME = "Emotion_Voice_Detection_Base.tflite"
private const val MODEL_FILENAME = "Emotion_Voice_Detection_Opensmile.tflite"
val OUTPUT_LABEL_MAP = hashMapOf<Int, String>(
    0 to "neutral",
    1 to "calm",
    2 to "happy",
    3 to "sad",
    4 to "angry",
    5 to "fearful",
    6 to "disgust",
    7 to "surprised"
)

data class ClassificationResults (
    val label: String,
    val probability: Float,
    val probabilities: HashMap<String, Float>
)

class AudioClassifier(context: Context) {
    private val interpreter = loadModel(context)

    fun classify(features: HashMap<String, Float>): ClassificationResults? {
        if(interpreter != null) {
            val input = arrayOf(FloatArray(88))

            Log.d(LOG_TAG, "input: ${input.size}, ${input[0].size}")

            val outputMap = hashMapOf<Int, Any>(
                0 to arrayOf(FloatArray(5))
            )

            Log.d(LOG_TAG, "input ${input.size} ${input[0].size} ${input[0][0]}")

            interpreter.runForMultipleInputsOutputs(input, outputMap)

            return this.outputToLabel(outputMap as HashMap<Int, Array<FloatArray>>)
        } else {
            Log.e(LOG_TAG, "model not loaded!")
            return null
        }
    }

    private fun outputToLabel(outputMap: HashMap<Int, Array<FloatArray>>): ClassificationResults {
        val resultMap = hashMapOf<String, Float>()
        val probabilities = outputMap[0]!![0]

        Log.d(LOG_TAG, "output size ${probabilities.size}")

        for(it in probabilities.withIndex()) {
            val label = OUTPUT_LABEL_MAP[it.index]!!
            val prob = it.value
            resultMap[label] = prob
        }

        val best = probabilities.maxOrNull()!!
        val bestIndex = probabilities.indexOfFirst {it == best}
        val bestLabel = OUTPUT_LABEL_MAP[bestIndex]!!

        return ClassificationResults(label = bestLabel, probability = best, resultMap)
    }

    private fun loadModel(context: Context): Interpreter? {
        try {
            val modelByteBuffer = loadModelFile(context.assets, MODEL_FILENAME)
            if(modelByteBuffer != null) {
                val tfLite =  Interpreter(modelByteBuffer)
                tfLite.resizeInput(0, IntArray(375))
                return tfLite
            }
            return null
        } catch (e: IOException) {
            Log.e(LOG_TAG, "could not load model: ${e.stackTrace}")
            return null
        }
    }

    /**
     * Memory-map the model file in Assets.
     * Copied from (1)
     * */
    @Throws(IOException::class)
    private fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer? {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}