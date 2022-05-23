package com.example.ms_project_android.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.ms_project_android.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val LOG_TAG = "RecognitionFragment"
private const val OPEN_SMILE_CONFIG = "MFCC12_0_D_A.func.conf"
private const val TF_MODEL_NAME = "Emotion_Voice_Detection_Opensmile_MFCC_2.tflite"
private enum class FAB_ICONS {
    MIC,
    STOP,
    NEXT
}


class RecognitionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var stateNext = false
    private var stateEmotion = -1
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
                this.startRun(view, fab)
            }
            fab.visibility = View.VISIBLE
            initView(view, fab)
        } else {
            Log.e(LOG_TAG, "Could not get activity")
        }

    }

    private fun startRun(view: View, fab: FloatingActionButton) {
        // test run = -1
        // emotion = 0 - 4
        // next = 5

        Log.d(LOG_TAG, "startRun $stateNext $stateEmotion")

        if(stateNext) {
            if(stateEmotion > 4) {
                fab.visibility = View.GONE
                findNavController().navigate(R.id.action_recognitionFragment_to_shareFragment)
            } else {
                initView(view, fab)
            }
            stateNext = false
        } else {
            if(mAudioRecorder.isRecording()) {
                stopRecording(view, fab)
                runClassification(view)
                stateNext = true
                stateEmotion += 1
//                stateEmotion += 5
            } else {
                startRecording(view, fab)
            }
        }
    }

    private fun initView(view: View, fab: FloatingActionButton) {
        val tvTitle = view.findViewById<TextView>(R.id.recognition_title)
        val tvText = view.findViewById<TextView>(R.id.recognition_text)
        val tvRecordState = view.findViewById<TextView>(R.id.record_state)
        val tvRecordDuration = view.findViewById<TextView>(R.id.record_duration)
        val table = view.findViewById<TableLayout>(R.id.recognition_table)
        val emoState = stateEmotion

        table.removeAllViews()

        tvTitle.text = parseHtml(emoState, title = true)
        tvText.text = parseHtml(emoState)
        tvRecordState.text = "NO"
        tvRecordDuration.text = "0"

        setFabImage(fab, icon = FAB_ICONS.MIC)
    }

    private fun setFabImage(fab: FloatingActionButton, icon: FAB_ICONS) {
        val drawableId = when(icon) {
            FAB_ICONS.MIC -> R.drawable.ic_baseline_mic_24
            FAB_ICONS.NEXT -> R.drawable.ic_baseline_navigate_next_24
            FAB_ICONS.STOP -> R.drawable.ic_baseline_stop_24
        }

        val drawable = resources.getDrawable(drawableId, null)
        fab.setImageDrawable(drawable)
    }

    private fun parseHtml(emotionId: Int, title: Boolean = false): Spanned {
        val suffix = if (title) "_name" else ""
        val middlePart = if(emotionId < 0) "" else "_emo$emotionId"
        val stringName = "recognition${middlePart}_task$suffix"

        Log.d(LOG_TAG, "parseHTML $stringName")

        val stringId = resources.getIdentifier(stringName, "string", mConfig.packageName)
        val string = getString(stringId)
        return Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    }

    private fun updateRecordDuration(view: TextView) {
        val handler = Handler()
        handler.postDelayed(Runnable {
            if(mAudioRecorder.isRecording()) {
                val duration = (mAudioRecorder.getDuration()).toFloat() / 1000
                view.text = String.format("%.02f / 60.00 sec", duration)
                updateRecordDuration(view)
            }
        }, 100)
    }

    private fun startRecording(view: View, fab: FloatingActionButton) {
        mAudioRecorder.startRecording()

        val tvRecordState = view.findViewById<TextView>(R.id.record_state)
        val tvRecordDuration = view.findViewById<TextView>(R.id.record_duration)

        tvRecordState.text = "YES"
        setFabImage(fab, icon = FAB_ICONS.STOP)

        updateRecordDuration(tvRecordDuration)
    }
    private fun stopRecording(view: View, fab: FloatingActionButton) {
        mAudioRecorder.stopRecording()
        val tvRecordState = view.findViewById<TextView>(R.id.record_state)
        tvRecordState.text = "NO"

        setFabImage(fab, icon = FAB_ICONS.NEXT)
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

                saveResults(results)
            }
        }
    }

    private fun saveResults(results: ClassificationResults) {
        mAudioClassifier.saveResults(results, stateEmotion)
    }
}