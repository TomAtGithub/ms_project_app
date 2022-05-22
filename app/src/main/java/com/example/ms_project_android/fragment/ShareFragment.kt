package com.example.ms_project_android.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.ms_project_android.GlobalConfig
import com.example.ms_project_android.R
import java.io.File


private const val LOG_TAG = "ShareFragment"

class ShareFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.btn_share)
        btn.setOnClickListener {
            this.composeEmail()
        }
    }

    private fun composeEmail() {
        val config = GlobalConfig.getInstance()
        val addresses = arrayOf("tr9865@student.uni-lj.si", "dv6968@student.uni-lj.si")
        val subject = "ms-project_evaluation"
        val file = File(config.evaluationCsvPath)
        val intent = Intent(Intent.ACTION_SENDTO)

        Log.d(LOG_TAG, "Share CSV: ${file.exists()}")

        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, file.readText())
        startActivity(intent)
    }

}