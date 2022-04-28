package com.example.ms_project_android

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.lang.Error

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val LOG_TAG = "RecordedFragment"

interface RecordFragmentInterface {
    fun togglePlayback(): Boolean
}

/**
 * A simple [Fragment] subclass.
 * Use the [RecordedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordedFragment : Fragment(), RecordFragmentInterface {
    private var stats: RecordStats? = null

    override fun togglePlayback(): Boolean {
        throw Error("not implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stats = it.getSerializable("stats") as RecordStats?
        }

        Log.d(LOG_TAG, stats.toString())
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
        val infoView = view.findViewById<TextView>(R.id.textViewInfo)
        val btn = view.findViewById<Button>(R.id.button2)
        val mainActivity = activity as RecordFragmentInterface

        btn.setOnClickListener { view ->
            val isPlaying = mainActivity.togglePlayback()
            btn.text = when(isPlaying) {
                true -> "PAUSE"
                false -> "PLAY"
            }
        }

        infoView.text = stats.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(stats: RecordStats) =
            RecordedFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("stats", stats)
                }
            }
    }

    fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}