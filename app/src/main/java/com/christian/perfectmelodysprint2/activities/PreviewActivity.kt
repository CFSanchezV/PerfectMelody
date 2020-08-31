package com.christian.perfectmelodysprint2.activities

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.christian.perfectmelodysprint2.AudioManager
import com.christian.perfectmelodysprint2.R
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {
    private val TAG = "PreviewActivity"
    private lateinit var audioManager: AudioManager

    private var mp: MediaPlayer? = null
    private var totalTime: Int = 0

    var myFileId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        audioManager = AudioManager(this)

        val id = intent.getStringExtra("fileName")
        Log.d(TAG, "filename is $id")
        Toast.makeText(this, "audio in ${audioManager.filePathForId(id)}", Toast.LENGTH_SHORT).show()

        myFileId = id

        audioManager.startPlayback(id)
        mp = audioManager.mediaPlayer

        mp?.isLooping = false
        mp?.setVolume(0.5f, 0.5f)
        totalTime = mp!!.duration

        playBtn.setBackgroundResource(R.drawable.stop)

        // Volume Bar
        volumeBar?.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekbar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        var volumeNum = progress / 100.0f
                        mp!!.setVolume(volumeNum, volumeNum)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        // Position Bar
        positionBar?.max = totalTime
        positionBar?.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp!!.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        // Thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp!!.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            // Update positionBar
            positionBar?.progress = currentPosition

            // Update Labels
            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel?.text = elapsedTime

            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel?.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun playBtnClick(v: View) {

        if (mp!!.isPlaying) {
            // Stop
            mp?.pause()
            v.setBackgroundResource(R.drawable.play)

        } else {
            // Start
            mp!!.start()
            v.setBackgroundResource(R.drawable.stop)
        }
    }

}
