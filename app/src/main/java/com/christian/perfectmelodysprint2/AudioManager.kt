package com.christian.perfectmelodysprint2

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.IOException

class AudioManager(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    fun startPlayback(id: String): Boolean {
        val path = filePathForId(id)
        if (File(path).exists()) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(path)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            return true
        }
        return false
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun filePathForId(id: String): String { //Once Kotlin has proper UInt type change this
        val path = Environment.getExternalStorageDirectory().absolutePath + "/perfectmelodyV2/"
        val dir = File(path)
        if(!dir.exists()){
            try {
                dir.mkdirs()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return "$path$id.mp3"

        //return Environment.getExternalStorageDirectory().absolutePath +"/perfectmelodyV2/"+ id +".mp3"
    }

    fun startRecording(id: String): Boolean {

        //check the device has a microphone
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {

            //create new instance of MediaRecorder
            mediaRecorder = MediaRecorder()

            //specify source of audio (Microphone)
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)

            //specify file type and compression format
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            //specify audio sampling rate and encoding bit rate (48kHz and 128kHz respectively)
            mediaRecorder?.setAudioSamplingRate(48000)
            mediaRecorder?.setAudioEncodingBitRate(128000)

            //specify where to save
            val fileLocation = filePathForId(id)
            mediaRecorder?.setOutputFile(fileLocation)

            //record
            mediaRecorder?.prepare()
            mediaRecorder?.start()

            return true
        } else {
            return false
        }
    }

    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }


}