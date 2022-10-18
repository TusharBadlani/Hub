package cessini.technology.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import cessini.technology.cvo.notificationmodels.NotifResponse

class ProfileNotifBuilder(context: Context, CHANNEL_ID: String) :
    NotificationBuilder(context, CHANNEL_ID) {

    override fun buildNotification(notifData: NotifResponse) {

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.home_flow)
            .createPendingIntent()

        val largeIcon: Bitmap =
            ContextCompat.getDrawable(context, R.drawable.notif)?.toBitmap()!!

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.notifications_icon)
            .setContentTitle(notifData.title)
            .setContentText(notifData.desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that is received
            notify(notifData.id, builder.build())
        }
    }

    override fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Profile Notif Channel"
            //(TODO) Change the Notification channel description
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}