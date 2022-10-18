package cessini.technology.cvo.notificationmodels

import com.google.gson.annotations.SerializedName

data class NotifResponse(
    val id: Int = 0,
    val desc: String = "",
    val title: String = "",
    val data: String = "",
    val event: String = "",
    val timestamp: String = "",
    @SerializedName("event_details") val eventDetails: EventDetails = EventDetails(),
    @SerializedName("event_id") val eventId: String = "",
)

