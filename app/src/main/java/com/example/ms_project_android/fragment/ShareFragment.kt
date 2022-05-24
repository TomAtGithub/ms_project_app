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
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.ms_project_android.BuildConfig
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

        val gender_btn = view.findViewById<RadioGroup>(R.id.btn_gender)
        val age_btn = view.findViewById<RadioGroup>(R.id.btn_age)

        val btn = view.findViewById<Button>(R.id.btn_share)
        btn.setOnClickListener {
            // Read the gender from the radio group
            var gender: String
            val gender_id = gender_btn.checkedRadioButtonId
            if (gender_id == -1) gender = ""
            else {
                val selectedBtn = view.findViewById<RadioButton>(gender_id)
                gender = selectedBtn.text.toString()
            }

            // Read the age from the radio group
            var age: String
            val age_id = age_btn.checkedRadioButtonId
            if (age_id == -1) age = ""
            else {
                val selectedBtn = view.findViewById<RadioButton>(age_id)
                age = selectedBtn.text.toString()
            }

            this.composeEmail(age, gender)
        }
    }

    private fun composeEmail(age: String, gender: String) {
        val config = GlobalConfig.getInstance()
        val addresses = arrayOf("tr9865@student.uni-lj.si", "dv6968@student.uni-lj.si")
        val subject = "ms-project_evaluation"
        val file = File(config.evaluationCsvPath)
        val encoderFile = File(config.encoderEvaluationCsvPath)
        val text = "Gender: " + gender + "\nAge: " + age + "\n\nBasic model:\n" + file.readText() + "\n\nAutoencoder model:\n" + encoderFile.readText()
        val intent = Intent(Intent.ACTION_SENDTO)

        Log.d(LOG_TAG, "Share CSV: ${file.exists()}")

        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(intent)

    }

}