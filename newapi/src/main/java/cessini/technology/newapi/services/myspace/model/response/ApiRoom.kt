package cessini.technology.newapi.services.myspace.model.response

import com.google.gson.annotations.SerializedName

data class ApiGetRoom(
    val data: ApiRoomData = ApiRoomData(),
)

data class ApiRoomData(
    val room: ApiRoom = ApiRoom(),
)

data class ApiRoom(
    @SerializedName(value = "_id") val id: String = "",
    val creator: ApiCreator = ApiCreator(),
    val title: String = "",
    val schedule: Long = 0L,
    val private: Boolean = false,
    val live: Boolean = false,
    @SerializedName(value = "room_code") val code: String = "",
    val listeners: List<ApiListeners> = emptyList(),
    val category: List<String> = emptyList()
)

data class ApiListeners(
    @SerializedName(value = "_id") val profileId: String = "",
    @SerializedName(value = "name") val profileName: String = "",
    @SerializedName(value = "profile_picture") val profilePicture: String = "",
    @SerializedName(value = "channel_name") val profileChannelName: String = "",
)

data class ApiCreator(
    @SerializedName(value = "_id") val id: String = "",
    val name: String = "",
    @SerializedName(value = "profile_picture") val profilePicture: String = "",
    @SerializedName(value = "channel_name") val channelName: String = "",
)
