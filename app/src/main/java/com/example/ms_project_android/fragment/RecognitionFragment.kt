package com.example.ms_project_android.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.marginLeft
import com.example.ms_project_android.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val LOG_TAG = "RecognitionFragment"
private const val OPEN_SMILE_CONFIG = "MFCC12_0_D_A.func.conf"
private const val TF_MODEL_NAME = "Emotion_Voice_Detection_Opensmile_MFCC_2.tflite"


class RecognitionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var state = -1
    private lateinit var mConfig: Config
    private lateinit var mAudioRecorder: AudioRecorder
    private lateinit var mFeatureExtractor: FeatureExtractor
    private lateinit var mAudioClassifier: AudioClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recognition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mConfig = GlobalConfig.getInstance()

        mAudioRecorder = AudioRecorder(
            filePath = mConfig.recordPath
        )
        mFeatureExtractor = FeatureExtractor(
            openSmileConfigPath = mConfig.openSmileConfigPath,
            outCsvPath = mConfig.featuresCsvPath
        )

        val mainActivity = activity
        if(mainActivity != null) {
            val fab = mainActivity.findViewById<FloatingActionButton>(R.id.fab)

            mAudioClassifier = AudioClassifier(mainActivity)

            fab.setOnClickListener {
                this.startRun(view)
            }
            fab.visibility = View.VISIBLE
        } else {
            Log.e(LOG_TAG, "Could not get activity")
        }

    }

    fun startRun(view: View) {
        // test run = -1
        // emotion = 0 - 5
        // next = 6

        Log.d(LOG_TAG, "startRun $view")

        if(mAudioRecorder.isRecording()) {
            stopRecording(view)
            runClassification(view)
        } else {
//            if(state < 0) {
//
//            } else if(state <= 5) {
//
//            } else {
//
//            }
            startRecording(view)
            state = +1
        }
    }

    private fun updateRecordDuration(view: TextView) {
        val handler = Handler()
        handler.postDelayed(Runnable {
            if(mAudioRecorder.isRecording()) {
                val duration = (mAudioRecorder.getDuration()).toFloat() / 1000
                view.text = String.format("%.02f", duration)
                Log.d(LOG_TAG, "timeDiff $duration")
                updateRecordDuration(view)
            }
        }, 100)
    }

    private fun startRecording(view: View) {
        mAudioRecorder.startRecording()

        val tvRecordState = view.findViewById<TextView>(R.id.record_state)
        val tvRecordDuration = view.findViewById<TextView>(R.id.record_duration)

        tvRecordState.text = "YES"

        updateRecordDuration(tvRecordDuration)
    }
    private fun stopRecording(view: View) {
        mAudioRecorder.stopRecording()
        val tvRecordState = view.findViewById<TextView>(R.id.record_state)
        tvRecordState.text = "NO"
    }

    private fun runClassification(view: View) {
        val table = view.findViewById<TableLayout>(R.id.recognition_table)
        table.removeAllViews()

        val ok = mFeatureExtractor.run(mConfig.recordPath)
        if(ok) {
            val results = mAudioClassifier.classify(mFeatureExtractor.getFeatures())
            if(results != null) {
                Log.d(LOG_TAG, "${results.probabilities}")

                for (entry in results.probabilities.entries) {
                    val row = TableRow(view.context)
                    val label = TextView(view.context)
                    val value = TextView(view.context)

                    row.layoutParams = TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    label.text = entry.key
                    value.text = String.format(" %.5f", entry.value)

                    row.addView(label)
                    row.addView(value)
                    table.addView(row)

                    Log.d(LOG_TAG, "$entry")
                }
            }
        }

    }

    private fun showResults(results: ClassificationResults?) {
        if(results != null) {

        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecognitionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecognitionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

object Stopwatch {
    inline fun elapse(callback: () -> Unit): Long {
        var start = System.currentTimeMillis()
        callback()
        return System.currentTimeMillis() - start
    }

    inline fun elapseNano(callback: () -> Unit): Long {
        var start = System.nanoTime()
        callback()
        return System.nanoTime() - start
    }
}