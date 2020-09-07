package com.christian.perfectmelodysprint2.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.christian.perfectmelodysprint2.AudioManager
import com.christian.perfectmelodysprint2.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var recHeldDown = false

    private val PERMISSIONS_REQ = 1
    /*
    val permissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    */

    private lateinit var audioManager: AudioManager

    private var recEnabled = true
    private var noRecordingYet = true

    private var dir: File? = null

    private var output: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(800)
        //switch from Splashscreen
        super.setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {

                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQ)

            } else {
                Log.d("Home", "Already granted access")
                //INIT UI and MediaRecorder
            }
        }

        createDir()

        audioManager = AudioManager(this)

        recImgButton.tag = "audioFile"
        recImgButton.setOnTouchListener(touchListener)

        fabPreview.setOnClickListener {
            if (!noRecordingYet) {
                //noRecordingYet = true
                val intent1 = Intent(applicationContext, PreviewActivity::class.java)
                intent1.putExtra("fileName", recImgButton.tag as String)
                startActivity(intent1)
            }
        }

        // Thread
        Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(2000)
                    runOnUiThread {
                        if (recHeldDown){
                            //scalingAnimation()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }).start()
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
                    Toast.makeText(this,"Debe otorgar permisos para grabado de audio para usar la aplicación.",Toast.LENGTH_SHORT).show();
                    //App will trigger error
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
        //dir = File(Environment.getExternalStorageDirectory().absolutePath + "/perfectmelodyV2/")
        dir = File(applicationContext.getExternalFilesDir(null)?.absolutePath + "/perfectmelodyV2/")

        if(!dir!!.exists() || !dir!!.isDirectory) {
            try {
                // create a File object for the parent directory and have the object build the directory structure, if needed.
                //val recorderDirectory = File(Environment.getExternalStorageDirectory().absolutePath + "/perfectmelodyV2/")
                //recorderDirectory.mkdirs()
                dir!!.mkdirs()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    private fun scalingAnimation(){
        val upscale: Animation = ScaleAnimation(
            1F,
            1.1F,
            1F,
            1.1F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        val downscale: Animation = ScaleAnimation(
            1.1F,
            1F,
            1.1F,
            1F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        // 1 second duration
        upscale.duration = 500
        downscale.duration = 500
        val animSet = AnimationSet(true)
        animSet.isFillEnabled = true
        animSet.addAnimation(upscale)
        animSet.addAnimation(downscale)
        // Launching animation set
        recImgButton.startAnimation(animSet)
    }

    private val touchListener = View.OnTouchListener { v: View?, event: MotionEvent? ->
        Boolean

        val id = v?.tag as String

        if (event?.action == MotionEvent.ACTION_DOWN) {
            recHeldDown = true

            if (recEnabled) { //recording
                val isRecording = audioManager.startRecording(id)
                if (isRecording) {
                    Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show()
                    v.background.colorFilter =
                        PorterDuffColorFilter(Color.RED, PorterDuff.Mode.DARKEN)
                    switchVisibility(isRecording)

                    //PorterDuff is a class with list of blending + compositing modes, named after the authors of a paper on the subject
                } else {
                    Toast.makeText(this, "No se pudo grabar", Toast.LENGTH_LONG).show()
                }

            }

            recEnabled = false

            return@OnTouchListener true
        }
        if (event?.action == MotionEvent.ACTION_UP) {
            recHeldDown = false

            if (!recEnabled) {
                audioManager.stopRecording()
                noRecordingYet = false
                switchVisibility(recEnabled)
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
            tvInstruc.visibility = View.VISIBLE
        }
        else {
            fabPreview.visibility = View.VISIBLE
            tvInstruc.visibility = View.INVISIBLE
        }

    }


}
