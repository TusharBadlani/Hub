package cessini.technology.newapi.services.myspace.model.body

import com.google.gson.annotations.SerializedName

data class RoomNameBody(
    @SerializedName(value = "room_code") val name: String,
)
