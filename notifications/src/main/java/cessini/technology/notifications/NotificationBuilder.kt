package cessini.technology.notifications

import android.content.Context
import cessini.technology.cvo.notificationmodels.NotifResponse

/** abstract class to define the implementation of every notification builder child class*/
abstract class NotificationBuilder(val context: Context, val CHANNEL_ID: String) {
    abstract fun buildNotification(notifData: NotifResponse)
    abstract fun createNotificationChannel()
}