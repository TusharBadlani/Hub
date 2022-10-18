package cessini.technology.newapi.services.myspace.model.response

import com.google.gson.annotations.SerializedName

data class ApiRoomName(
    val data: RoomNameData,
)

data class RoomNameData(
    @SerializedName(value = "unique_room_name") val name: String,
)
