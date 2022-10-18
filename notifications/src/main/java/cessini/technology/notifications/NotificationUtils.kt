package cessini.technology.notifications

import android.content.Context
import android.util.Log
import cessini.technology.cvo.extensions.fromJson
import cessini.technology.cvo.notificationmodels.NotifResponse
import com.google.gson.Gson

class NotificationUtils(val context: Context) {

    companion object {
        private const val TAG = "NotificationUtils"
        private const val VIDEO_CHANNEL_ID = "0"
        private const val PROFILE_CHANNEL_ID = "1"
        private const val VIDEO_EVENT = "video"
        private const val PROFILE_EVENT = "profile"
        private const val STORY_EVENT = "story"

        var firstVideoNotif = true
        var firstProfileNotif = true
    }


    fun parseAndTrigger(message: String?) {

        val notifData: NotifResponse = Gson().fromJson(message!!)


        /** creating notification object based on the event type */
        when (notifData.event) {
            VIDEO_EVENT, STORY_EVENT -> {
                Log.i(TAG, "Video notification event detected")
                val notif = VideoNotifBuilder(context, VIDEO_CHANNEL_ID)

                if (firstVideoNotif) {
                    firstProfileNotif = false
                    notif.createNotificationChannel()
                }
                notif.buildNotification(notifData)
            }

            PROFILE_EVENT -> {
                Log.i(TAG, "Profile notification event detected")
                val notif = ProfileNotifBuilder(context, PROFILE_CHANNEL_ID)

                if (firstProfileNotif) {
                    firstProfileNotif = false
                    notif.createNotificationChannel()
                }

                notif.buildNotification(notifData)
            }

            else -> {
                Log.e(TAG, "Unknown notification event detected")
            }
        }

    }
}