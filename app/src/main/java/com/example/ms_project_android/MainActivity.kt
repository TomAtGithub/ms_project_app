/*
    File is inspired by this tutorial: https://developer.android.com/guide/topics/media/mediarecorder#kotlin
 */

package com.example.ms_project_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.audeering.opensmile.OpenSmileAdapter
import com.example.ms_project_android.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import kotlin.experimental.and

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAudioClassifier: AudioClassifierOld

   // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var navState = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        val navController = navHostFragment.findNavController()
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
//        binding.fab.setOnClickListener { view -> this.onFab(view, navController) }

        GlobalConfig.init(this, externalCacheDir?.path!!)
        val csvHandler = CSVHandler()
        csvHandler.deleteFile(GlobalConfig.getInstance().evaluationCsvPath)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun onFab(view: View, navController: NavController) {
        when (navState) {
            1 -> {
                navState = 2
                navController.navigate(R.id.action_firstFragment_to_recordingFragment)
            }
            2 -> {
                navState = 3
                navController.navigate(R.id.action_recordingFragment_to_recordedFragment)
            }
            3 -> {
                navState = 2
                navController.navigate(R.id.action_recordedFragment_to_recordingFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        testRun(this)
    }

    private fun testRun(
        context: Context,
        recordAudio: Boolean = false,
        createMfcc: Boolean = true,
        classify: Boolean = true)
    {
        val logTag = "TEST_RUN"
        val config = GlobalConfig.getInstance()

        if(recordAudio) {
            val audioPath = config.recordPath
            val mAudioRecorder = AudioRecorder(audioPath)
            val duration: Long = 5000
            val handler = Handler()

            mAudioRecorder.startRecording()
            Log.d(logTag, "recording started for $duration ms")
            handler.postDelayed(Runnable {
                mAudioRecorder.stopRecording()
                Log.d(logTag, "recording finished")
                testRun(context, recordAudio = false)
            }, duration)

            return
        }

        val featureExtractor = FeatureExtractor(
            openSmileConfigPath = config.openSmileConfigPath,
            outCsvPath = config.evaluationCsvPath
        )
        if(createMfcc) {
            val audioPath = Utils.getAsset(context, "audio/test.wav").absolutePath
            val ok = featureExtractor.run(audioPath)
            val hrState = if(ok) {
                "SUCCESS"
            } else {
                "FAILED"
            }

            Log.d(logTag, "feature extraction $hrState")
        }
        val features = featureExtractor.getFeatures()
        Log.d(logTag, "features loaded ${features.contentDeepToString()}")

        if(classify) {
            val audioClassifier = AudioClassifier(context)
            val results = audioClassifier.classify(features)
            if(results != null) {
                Log.d(logTag, "classification: audio classified as ${results.label} with probability ${results.probability}")
                Log.d(logTag, "classification: probabilities ${results.probabilities}")
            } else {
                Log.d(logTag, "classification: FAILED")
            }
        }
    }
}