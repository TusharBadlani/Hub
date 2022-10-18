package cessini.technology.newapi.model

import com.google.gson.annotations.SerializedName

data class ApiNotificationWhenLoggedIn(
    val message: String,
    val data: DataLN,
    val status: Boolean
)
data class DataLN(
    @SerializedName( "my_world_notifications")
    val myWorldNotifications: List<MyWorldNotification>,

    @SerializedName("user_notification")
    val userNotification: Any? = null
)


