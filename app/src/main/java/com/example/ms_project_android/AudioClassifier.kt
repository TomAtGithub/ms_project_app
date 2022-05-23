/**
 * Uses code of: https://bitbucket.org/pbdfrita/msfrita_voice_recognition.git (1)
 */

package com.example.ms_project_android

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.HashMap


private const val LOG_TAG = "AudioClassifier"
//private const val MODEL_FILENAME = "Emotion_Voice_Detection_Base.tflite"
//private const val MODEL_FILENAME = "Emotion_Voice_Detection_Opensmile.tflite"
private const val MODEL_FILENAME = "Emotion_Voice_Detection_Opensmile_MFCC_2.tflite"
private const val AUTOENCODER_MODEL_FILENAME = "Emotion_Voice_Detection_Opensmile_autoencoder.tflite"
private const val AUTOENCODER_FILENAME = "mfcc_encoder.tflite"

private const val LATENT_FEATURES = 16

val OUTPUT_LABEL_MAP = hashMapOf<Int, String>(
    0 to "neutral",
    1 to "happiness",
    2 to "sadness",
    3 to "anger",
    4 to "fear"
)

private const val N_CLASSES = 5

data class ClassificationResults (
    val label: String,
    val probability: Float,
    val probabilities: HashMap<String, Float>
)

class AudioClassifier(context: Context, autoencoder: Boolean = false) {
    private val autoencoder = autoencoder
    private val interpreter = loadModel(context)
    private val encoder_interpreter = loadAutoencoder(context)

    fun classify(features: Array<FloatArray>): ClassificationResults? {
        if(interpreter != null) {

            // model uses only the first 288 columns (mfcc features only)
//            val featureVector = FloatArray(684)
            //val featureSet = features.values.toFloatArray().copyOfRange(0, 228)
//            System.arraycopy(featureSet, 0, featureVector, 0, featureSet.size)
            //val input = arrayOf(arrayOf(featureSet))

            /*Log.d(LOG_TAG, "input shape: (${input[0].size}, ${input[0][0].size})")
            Log.d(LOG_TAG, "input features: ${input.contentDeepToString()}")*/
            Log.d(LOG_TAG, "input features: ${features.contentDeepToString()}")

            var input_features: Array<FloatArray>

            if (this.autoencoder) {
                val latentMap = hashMapOf<Int, Any>(
                    0 to arrayOf(FloatArray(LATENT_FEATURES)),
                    1 to arrayOf(FloatArray(LATENT_FEATURES))
                )
                if (encoder_interpreter != null) {
                    encoder_interpreter.runForMultipleInputsOutputs(features, latentMap)
                }

                Log.d(LOG_TAG, "latent features (means): ${(latentMap[1] as Array<FloatArray>).contentDeepToString()}")
                Log.d(LOG_TAG, "latent sd: ${(latentMap[0] as Array<FloatArray>).contentDeepToString()}")

                input_features = latentMap[1] as Array<FloatArray>
            }
            else input_features = features

            val outputMap = hashMapOf<Int, Any>(
                0 to arrayOf(FloatArray(5))
            )

            interpreter.runForMultipleInputsOutputs(input_features, outputMap)

            return this.outputToLabel(outputMap as HashMap<Int, Array<FloatArray>>)
        } else {
            Log.e(LOG_TAG, "model not loaded!")
            return null
        }
    }

    private fun outputToLabel(outputMap: HashMap<Int, Array<FloatArray>>): ClassificationResults {

        val resultMap = hashMapOf<String, Float>()
        val probabilities = outputMap[0]!![0]

        Log.d(LOG_TAG, "output raw: ${outputMap[0]!![0].contentToString()}")

        for(it in probabilities.withIndex()) {
            val label = OUTPUT_LABEL_MAP[it.index]!!
            val prob = it.value
            resultMap[label] = prob
        }

        val best = probabilities.maxOrNull()!!
        val bestIndex = probabilities.indexOfFirst {it == best}
        val bestLabel = OUTPUT_LABEL_MAP[bestIndex]!!

        Log.d(LOG_TAG, "output labeled: $resultMap")

        return ClassificationResults(label = bestLabel, probability = best, resultMap)
    }

    private fun loadModel(context: Context): Interpreter? {
        try {
            var modelByteBuffer: MappedByteBuffer? = null
            if (this.autoencoder) {
                modelByteBuffer = loadModelFile(context.assets, AUTOENCODER_MODEL_FILENAME)
            }
            else {
                modelByteBuffer = loadModelFile(context.assets, MODEL_FILENAME)
            }
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

    private fun loadAutoencoder(context: Context): Interpreter? {
        if (this.autoencoder.not()) return null

        try {
            val modelByteBuffer = loadModelFile(context.assets, AUTOENCODER_FILENAME)
            if(modelByteBuffer != null) {
                val tfLite =  Interpreter(modelByteBuffer)
                tfLite.resizeInput(0, IntArray(375))
                return tfLite
            }
            return null
        } catch (e: IOException) {
            Log.e(LOG_TAG, "could not load autoencoder: ${e.stackTrace}")
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