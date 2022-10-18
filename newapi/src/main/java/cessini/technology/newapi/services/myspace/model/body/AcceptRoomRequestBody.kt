package cessini.technology.newapi.services.myspace.model.body

import com.google.gson.annotations.SerializedName

data class AcceptRoomRequestBody(
    @SerializedName(value = "room_code") val roomName: String,
    val listeners: List<String>,
)
