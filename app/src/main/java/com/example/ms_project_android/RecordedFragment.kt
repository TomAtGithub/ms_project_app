package com.example.ms_project_android

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.lang.Error
import kotlin.experimental.and
import kotlin.math.log

private const val LOG_TAG = "RecordedFragment"

class RecordedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recorded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(LOG_TAG,"VIEW CREATED")



//        val btn = view.findViewById<Button>(R.id.button2)
//        val mainActivity = activity as RecordFragmentInterface
//
//        btn.setOnClickListener { view ->
//            val isPlaying = mainActivity.togglePlayback()
//            btn.text = when(isPlaying) {
//                true -> "PAUSE"
//                false -> "PLAY"
//            }
//        }


    }

    override fun onStart() {
        super.onStart()

//        Log.d(LOG_TAG, "ON STARTED")
//        val context = this.activity
//        var infoText = "ERROR"
//        if(context != null) {
//            val infoTextView = context.findViewById<TextView>(R.id.textViewInfo)
//            val featureExtractor = FeatureExtractor(context)
//            val ok = featureExtractor.run(Config.getRecordPath(context))
//
//            if(ok) {
//                val features = featureExtractor.getFeatures()
//                val audioClassifier = AudioClassifier(context)
//                val results = audioClassifier.classify(features)
//
//                if(results != null) {
//                    infoText = "Label: ${results.label}, Probability: ${results.probability}\n${results.probabilities}"
//                }
//            }
//            infoTextView.text = infoText
//        }
    }
}