package cessini.technology.newapi.model

import com.google.gson.annotations.SerializedName

data class ApiGetNotification(
    val data: List<ApiNotification> = emptyList(),
)

data class ApiNotificationWrap(
    val notification: List<ApiNotification> = emptyList(),
)
//    val event: String = "",
//    val id: String? = null,
//    val message: String = "",
//    val image: String = "",
//    val time: String = "",
//    val seen: Boolean = false,
data class ApiNotification(
    val message: String,
    val data: DataN,
    val status: Boolean
)
data class DataN(
    @SerializedName( "my_world_notifications")
    val myWorldNotifications: List<MyWorldNotification>,
//    @SerializedName("user_notification")
//    val userNotification: Any
)
data class MyWorldNotification (
    val id: String,
    val message: String,
    val username: String,
    val profile_image: String
)
