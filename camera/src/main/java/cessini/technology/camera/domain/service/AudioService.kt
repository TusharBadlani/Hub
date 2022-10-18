package cessini.technology.camera.domain.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.session.PlaybackState.*
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import java.io.IOException

open class AudioService : Service() {
    companion object {
        private const val TAG = "AudioService"
    }

    private var mediaPlayer = MediaPlayer()

    private var prepared = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUrl = intent?.getStringExtra("URL")
        Log.d(TAG, "Audio Url: $audioUrl")

        when (intent?.action) {
            ACTION_PREPARE.toString() -> {
                val myUri: Uri? = audioUrl?.toUri() // initialize Uri here
                if (myUri != null) {
                    prepareMediaPlayer(myUri)
                }
            }
            ACTION_PAUSE.toString() -> {
                mediaPlayer.reset()
            }
            ACTION_PLAY.toString() -> {
                if (prepared) {
                    mediaPlayer.start()
                }
            }

        }
        return START_STICKY
    }


    private fun prepareMediaPlayer(uri: Uri) {
        prepared = false
        Log.d(TAG, "Song Url: $uri")
        if (mediaPlayer.isPlaying) {
            mediaPlayer.reset()
        }
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(this@AudioService, uri)
                prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            setOnPreparedListener {
                val audio: Audio = object : Audio {}
                Log.d("Camera", "Audio has been Prepared Successfully.")
                prepared = true
                audio.isPrepared(true, it)
                audio.getDuration(it.duration)
            }
            this.setOnCompletionListener {
                mediaPlayer.reset()
            }
        }
    }

    fun isPrepared(): Boolean {
        return prepared
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.reset()
    }

    interface Audio {
        fun isPrepared(isPrepared: Boolean, mediaPlayer: MediaPlayer): Boolean {
            return isPrepared
        }

        fun getDuration(duration: Int): Int {
            return duration
        }
    }

}