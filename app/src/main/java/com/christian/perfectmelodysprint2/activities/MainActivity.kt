package com.christian.perfectmelodysprint2.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.christian.perfectmelodysprint2.AudioManager
import com.christian.perfectmelodysprint2.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val PERMISSIONS_REQ = 1
    val permissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var audioManager: AudioManager

    private var recEnabled = true
    private var noRecordingYet = true

    //private var dir: File? = null

    private var output: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(800)
        //Splashscreen
        super.setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        createDir()
        //dir = File(Environment.getExternalStorageDirectory().absolutePath + "/perfectmelodyV2/")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {

                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQ)

            } else {
                Log.d("Home", "Already granted access")
                //INIT UI and MediaRecorder
            }
        }

        audioManager = AudioManager(this)

        recImgButton.tag = "audioFile"
        recImgButton.setOnTouchListener(touchListener)

        fabPreview.setOnClickListener {
            if (!noRecordingYet) {
                noRecordingYet = true
                val intent1 = Intent(applicationContext, PreviewActivity::class.java)
                intent1.putExtra("fileName", recImgButton.tag as String)
                startActivity(intent1)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQ -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    //INIT UI and MediaRecorder
                    // Permission is granted. Continue the action or workflow in your app.
                } else {
                    Log.d(TAG, "Permissions Failed")
                    Toast.makeText(this,"You must allow permission record audio to your mobile device.",Toast.LENGTH_SHORT).show();
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun requestPermissions() {
        val permissionsRequired = mutableListOf<String>()

        val hasRecordPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasRecordPermission) {
            permissionsRequired.add(Manifest.permission.RECORD_AUDIO)
        }

        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasStoragePermission) {
            permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsRequired.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsRequired.toTypedArray(),
                PERMISSIONS_REQ
            )
        }
    }

    private fun createDir() {
        try {
            // create a File object for the parent directory
            val recorderDirectory =
                File(Environment.getExternalStorageDirectory().absolutePath + "/perfectmelodyV2/")
            // have the object build the directory structure, if needed.
            recorderDirectory.mkdirs()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        /*
        if (dir!!.exists() && dir!!.listFiles()?.size!! != 0) {
            val theFileName = dir!!.listFiles()?.get(0)?.name
            output = Environment.getExternalStorageDirectory().absolutePath + "/perfectmelodyV2/" + theFileName + ".mp3"
        }
        */
    }

    private val touchListener = View.OnTouchListener { v: View?, event: MotionEvent? ->
        Boolean

        val id = v?.tag as String

        if (event?.action == MotionEvent.ACTION_DOWN) {

            if (recEnabled) { //recording
                val isRecording = audioManager.startRecording(id)
                if (isRecording) {
                    Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show()
                    v.background.colorFilter =
                        PorterDuffColorFilter(Color.RED, PorterDuff.Mode.DARKEN)
                    //switchVisibility(isRecording)

                    //PorterDuff is a class with list of blending + compositing modes, named after the authors of a paper on the subject
                } else {
                    Toast.makeText(this, "No se pudo grabar", Toast.LENGTH_LONG).show()
                }

            }

            recEnabled = false

            return@OnTouchListener true
        }
        if (event?.action == MotionEvent.ACTION_UP) {

            if (!recEnabled) {
                audioManager.stopRecording()
                noRecordingYet = false
                fabPreview.visibility = View.VISIBLE
                Toast.makeText(this, "Grabacion en ${audioManager.filePathForId(id)}", Toast.LENGTH_SHORT).show()
            }

            v.background.clearColorFilter()
            recEnabled = true

            return@OnTouchListener true
        }

        false
    }

    private fun switchVisibility(recording: Boolean) {
        if (recording){
            fabPreview.visibility = View.INVISIBLE

        }
        else {
            fabPreview.visibility = View.VISIBLE

        }

    }


}
