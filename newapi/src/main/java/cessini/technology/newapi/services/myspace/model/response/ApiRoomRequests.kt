package cessini.technology.newapi.services.myspace.model.response

import com.google.gson.annotations.SerializedName

data class ApiRoomRequests(
    val data: List<RoomRequestData> = emptyList(),
)

data class RoomRequestData(
    @SerializedName(value = "listeners_pending") val requests : List<ApiRequestProfile>,
)

data class ApiRequestProfile(
    @SerializedName(value = "_id") val id: String,
    @SerializedName(value = "profile_picture") val picture: String,
    @SerializedName(value = "channel_name") val channel: String,
    val name: String,
)
