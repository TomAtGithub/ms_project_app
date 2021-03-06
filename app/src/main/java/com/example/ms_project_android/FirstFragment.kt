package com.example.ms_project_android

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ms_project_android.databinding.ActivityMainBinding
import com.example.ms_project_android.databinding.FragmentFirstBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

private const val LOG_TAG = "AudioClassifierFragment"

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Log.d(LOG_TAG, activity.toString())
//
//        var fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
//        if(fab != null) {
//            fab.setOnClickListener { view ->
//                Log.d(LOG_TAG, "HELLO")
//            }
//        } else {
//            Log.d(LOG_TAG, "FAB NOT FOUND")
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}