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

private fun getLE(buffer: ByteArray, pos: Int, numBytes: Int): Long {
    var pos = pos
    var numBytes = numBytes
    numBytes--
    pos += numBytes
    var `val`: Long = (buffer[pos] and 0xFF.toByte()).toLong()
    for (b in 0 until numBytes) `val` = (`val` shl 8) + (buffer[--pos] and 0xFF.toByte())
    return `val`
}

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAudioClassifier: AudioClassifier

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

        val inputStream = this.resources.openRawResource(R.raw.test)
        var buffer = ByteArray(12)
        inputStream.read(buffer, 0, 12)
        var header = ByteArray(4)
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()

        val hChunkId = getLE(header, 0, 4)
        val hChunkId2 = getLE(buffer, 0, 4)
        val chunkId: Long = 1179011410
        val chunkEquals = hChunkId == chunkId

        Log.d(LOG_TAG, "CHUNK_ID: $chunkId, $hChunkId, $chunkEquals, $hChunkId2")


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

    private fun onFab2(view: View, navController: NavController) {

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
//            val action =
        } else {
            val action = RecordedFragmentDirections.actionRecordedFragmentToRecordingFragment()
            navController.navigate(action)
        }

    }

    override fun onStart() {
        super.onStart()
//        recordTest(this)
        opensmile(this)
    }

    /**
     * copies a file to a given destination
     * @param filename the file to be copied
     * @param dst destination directory (default: cacheDir)
     */
    private fun cacheAsset(filename: String, dst: String = cacheDir.absolutePath): String {
        val pathname = "$dst/$filename"
        val outfile = File(pathname).apply { parentFile?.mkdirs() }
        assets.open(filename).use { inputStream ->
            FileOutputStream(outfile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Log.d(LOG_TAG, "copy $pathname to $outfile")
        return outfile.path
    }

    private fun cacheAssetDir(dir: String): String {
        val paths = assets.list(dir)

        Log.d(LOG_TAG, "cacheAssetsDir path: $dir, ${paths?.size}")

        if(paths != null) {
            for (path in paths) {
                Log.d(LOG_TAG, "cacheAssetsDir path: $path")
            }
        } else {
            Log.d(LOG_TAG, "cacheAssetsDir path: null")
        }

        return ""
    }

    private fun opensmile(context: Context) {
//        val osConfig = context.assets.open("config/MFCC12_0_D_A.conf")
//        val osConfig = context.resources.assets.open("config/MFCC12_0_D_A.conf")
//        val config = osConfig.read()

//        val dir = File(cacheDir.path)
//        for(file in dir.listFiles()) {
//            Log.d(LOG_TAG, "cache dir ${file.absolutePath}")
//        }
        val LOG_TAG_H = "$LOG_TAG:OPENSMILE"
        val cachePath = context.externalCacheDir?.path!!
//        val cachePath = context.cacheDir?.path!!
        val configPath = cacheAsset("config/MFCC12_0_D_A.func.conf", cachePath)
//        val configPath = cacheAsset("config/eGeMAPS_v02_Complete_v2.conf", cachePath)
        val configBufferPath = cacheAsset("config/BufferModeLive.conf.inc", cachePath)
        val configBufferRbPath = cacheAsset("config/BufferModeRb.conf.inc", cachePath)
        val configBufferRbLgPath = cacheAsset("config/BufferModeRbLag.conf.inc", cachePath)
        val configBufferFrameModePath = cacheAsset("config/FrameModeFunctionalsLive.conf.inc", cachePath)
//        val configPath = cacheAsset("config/test.config", cachePath)
//        val configPath = cacheAsset("config/mfcc.config", cachePath)
        val wavPath = cacheAsset("audio/test.wav", cachePath)
//        val wavPath = cacheAsset("audio/happy_test.wav", cachePath)
        val csvPath = "$cachePath/audio/mfcc_2.func.csv"
        val csvPathEge = "$cachePath/audio/egemaps3.csv"
        val params = hashMapOf<String, String?>(
            "-I" to wavPath,
            "-O" to csvPath,
//            "-bufferModeConf" to configBufferPath,
//            "-frameModeFunctionalsConf" to configBufferFrameModePath,
//            "-csvoutput" to csvPathEge,
            // "-end" to "10"
            //"-bufferModeRbConf" to configBufferRbPath
        )
        val loglevel = 3
        val debug = 1
        val consoleOutput = 1

        //cacheAssetDir("$cachePath/config/")

        val ose = OpenSmileAdapter()
        var state = ose.smile_initialize(configPath, HashMap(params), loglevel, debug, consoleOutput)
        Log.d(LOG_TAG_H, "state br $state")
        state = ose.smile_run()
        Log.d(LOG_TAG_H, "state ar $state")
//        val configPathName = cacheDir.path + "/config"
//        val inputStream = context.resources.openRawResource(R.raw.opensmile)
//        val isr = InputStreamReader(inputStream)
//        val mainConfig = isr.readText()
//        val filePath = context.getExternalFilesDir("records")?.path + "/test.wav"
////        val params = mapOf("-0" to filePath)
//        val params = hashMapOf<String, String?>("-0" to filePath)
//        val loglevel = 2
//        val debug = 0
//        val consoleOutput = 0
////        Log.d(LOG_TAG, "config: ${config.toString()}")
//
////        val config2 = context.resources.openRawResource(config)
////        val config3 = config2.read()
////        Log.d(LOG_TAG, "config: ${config2.toString()}")
////        Log.d(LOG_TAG, "config: ${config3.toString()}")
//        val ose = OpenSmileAdapter()
//        var state = ose.smile_initialize(mainConfig, params, loglevel, debug, consoleOutput)

    }

    private fun recordTest(context: Context) {
        Log.d(LOG_TAG, "RT: record test started")

//        val filePath = context.getExternalFilesDir("records")?.path + "/test.wav"
//        val file = File(filePath)
        val dirRecords = context.getExternalFilesDir("records")
        val files = dirRecords?.listFiles()
        if(files != null) {
            for (record in files) {
                Log.d(LOG_TAG, "RT: File: ${record.path}")
                val mfccExtractor = MFCCExtractor(40)
                val mfcc = mfccExtractor.getMeanMFCC(record.path)
                Log.d(LOG_TAG, "RT: MFCC classification finished")
            }
        } else {
            Log.d(LOG_TAG, "RT: files not found")
        }

//        Log.d(LOG_TAG, "RT: filepath: $filePath")
//        if(file.exists()) {
//            Log.d(LOG_TAG, "RT: file exists")
//            val mfccExtractor = MFCCExtractor(40)
//            val mfcc = mfccExtractor.getMeanMFCC(filePath)
//            Log.d(LOG_TAG, "RT: MFCC classification finished")
//        }
//        else {
//            Log.d(LOG_TAG, "RT: file not found")
//        }
    }

    private fun recordTest2(context: Context) {
        val fileName = "record10.wav"
        val filePath = context.externalCacheDir?.path + "/$fileName"
        val mAudioRecorder = AudioRecorder(context, filePath)
        val mfccExtractor = MFCCExtractor(40)
        val duration: Long = 5000

        Log.d(LOG_TAG, "MFCC classification started, recording wav file for $duration ms")
        mAudioRecorder.startRecording()

        val handler = Handler()
        handler.postDelayed(Runnable {
            // Actions to do after 10 seconds
            mAudioRecorder.stopRecording()
            val mfcc = mfccExtractor.getMeanMFCC(filePath)
            Log.d(LOG_TAG, "MFCC classification finished")
        }, duration)
    }
}
