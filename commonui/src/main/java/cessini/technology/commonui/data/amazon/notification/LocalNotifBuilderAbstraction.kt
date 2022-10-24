package cessini.technology.commonui.data.amazon.notification

import android.content.Context
import cessini.technology.cvo.notificationmodels.NotifResponse

abstract class LocalNotifBuilderAbstraction(val context: Context, val CHANNEL_ID: String) {
    abstract fun buildNotification(title:String, desc:String, id:Int)
    abstract fun createNotificationChannel()
}