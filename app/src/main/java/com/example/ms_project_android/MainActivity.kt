/*
    File is inspired by this tutorial: https://developer.android.com/guide/topics/media/mediarecorder#kotlin
 */

package com.example.ms_project_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.ms_project_android.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.IOException
import java.security.Permissions

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity(), RecordFragmentInterface {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAudioClassifier: AudioClassifier

   // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var navState = 1

    override fun togglePlayback(): Boolean {
        if(mAudioClassifier.isPlaying()) {
            mAudioClassifier.pause()
        } else {
            mAudioClassifier.play()
        }
        return mAudioClassifier.isPlaying()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


//        val navController = findNavController(R.id.frag_nav_controller)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        val navController = navHostFragment.findNavController()
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        mAudioClassifier = AudioClassifier(this)

//        var frag: Fragment = FirstFragment()
//        supportFragmentManager.beginTransaction().add(R.id.container, frag).commit()

//        binding.fab.setOnClickListener { view ->
//            val isRecording = mAudioClassifier.isRecording()
//            val nextFrag = if(isRecording) {
//                mAudioClassifier.stopRecording()
//                mAudioClassifier.load()
//                RecordedFragment.newInstance(mAudioClassifier.getStats())
//            } else {
//                mAudioClassifier.startRecording()
//                RecordingFragment()
//            }
//
//            supportFragmentManager.beginTransaction().remove(frag).add(R.id.container,
//                nextFrag
//            ).commit()
//
//            frag = nextFrag
//
//            Snackbar.make(view, "isRecording: $isRecording", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        
        binding.fab.setOnClickListener { view -> this.onFab(view, navController) }
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

        val isRecording = mAudioClassifier.isRecording()
        Log.d(LOG_TAG, "is recoding: $isRecording ${view.id} ${R.id.firstFragment}")

        if(navState == 1) {
            navState = 2
            navController.navigate(R.id.action_firstFragment_to_recordingFragment)
            mAudioClassifier.startRecording()
        }
        else if(mAudioClassifier.isRecording()) {
            mAudioClassifier.stopRecording()
            mAudioClassifier.load()
            val stats = mAudioClassifier.getStats()
            val action =
        } else {
            val action = RecordedFragmentDirections.actionRecordedFragmentToRecordingFragment()
            navController.navigate(action)
        }

    }
}
