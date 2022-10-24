package cessini.technology.commonui.services.screen_share

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cessini.technology.commonui.R
import cessini.technology.commonui.presentation.HomeActivity
import com.pedro.rtplibrary.base.DisplayBase
import com.pedro.rtplibrary.rtmp.RtmpDisplay
import com.pedro.rtplibrary.rtsp.RtspDisplay

class MediaProjectionService : Service() {
     val NOTIFICATION_CHANNEL_ID = "channel_id"
     val NOTIFICATION_ID = 77
     val NOTIFICATION_CHANNEL_NAME = "Screen Sharing"


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(channel)
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    companion object {
        private const val TAG = "DisplayService"
        private const val channelId = "rtpDisplayStreamChannel"
        const val notifyId = 123456
        var INSTANCE: MediaProjectionService? = null
    }
    private var notificationManager: NotificationManager? = null
    private var displayBase: DisplayBase? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        INSTANCE = this
        Log.i(TAG, "RTP Display service started")
        displayBase = RtmpDisplay(baseContext, true, connectCheckerRtp)
        displayBase?.glInterface?.setForceRender(true)
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_myworldintrologo)
            .setContentTitle("ScreenShare")
            .setContentText("Screen sharing running")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(
            NOTIFICATION_ID,
            notificationBuilder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        )

    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, HomeActivity::class.java), // GridActivity
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(text: String) {
        val notification = NotificationCompat.Builder(baseContext, channelId)
            .setSmallIcon(R.drawable.ic_myworldintrologo)
            .setContentTitle("RTP Display Stream")
            .setContentText(text)
            .setOngoing(false)
            .build()
        notificationManager?.notify(notifyId, notification)
    }

    private val connectCheckerRtp = object : ConnectCheckerRtp {

        override fun onConnectionStartedRtp(rtpUrl: String) {
        }

        override fun onConnectionSuccessRtp() {
            showNotification("Stream started")
            Log.i(TAG, "RTP service destroy")
        }

        override fun onNewBitrateRtp(bitrate: Long) {

        }

        override fun onConnectionFailedRtp(reason: String) {
            showNotification("Stream connection failed")
            Log.i(TAG, "RTP service destroy")
        }

        override fun onDisconnectRtp() {
            showNotification("Stream stopped")
        }

        override fun onAuthErrorRtp() {
            showNotification("Stream auth error")
        }

        override fun onAuthSuccessRtp() {
            showNotification("Stream auth success")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "RTP Display service destroy")
        stopStream()
        INSTANCE = null
    }

    fun prepareStreamRtp(endpoint: String, resultCode: Int, data: Intent) {
        stopStream()
        if (endpoint.startsWith("rtmp")) {
            displayBase = RtmpDisplay(baseContext, true, connectCheckerRtp)
            displayBase?.setIntentResult(resultCode, data)
        } else {
            displayBase = RtspDisplay(baseContext, true, connectCheckerRtp)
            displayBase?.setIntentResult(resultCode, data)
        }
        displayBase?.glInterface?.setForceRender(true)
    }

    fun startStreamRtp(endpoint: String) {
        if (displayBase?.isStreaming != true) {
            if (displayBase?.prepareVideo() == true && displayBase?.prepareAudio() == true) {
                displayBase?.startStream(endpoint)

                showNotification("You are streaming ")
            }
        } else {
            showNotification("You are already streaming")
        }
    }

    fun sendIntent(): Intent? {
        return displayBase?.sendIntent()
    }

    fun isStreaming(): Boolean {
        return displayBase?.isStreaming ?: false
    }

    fun isRecording(): Boolean {
        return displayBase?.isRecording ?: false
    }

    fun stopStream() {
        if (displayBase?.isStreaming == true) {
            displayBase?.stopStream()
            notificationManager?.cancel(notifyId)
        }
    }


}