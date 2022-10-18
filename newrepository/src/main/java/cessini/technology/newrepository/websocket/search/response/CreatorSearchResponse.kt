package cessini.technology.newrepository.websocket.search.response

import com.google.gson.annotations.SerializedName

data class CreatorSearchResponse(
    @SerializedName(value = "_id") val id: String,
    var name: String,
    @SerializedName(value = "channel_name") val channelName: String,
    @SerializedName(value = "profile_picture") val profilePicture: String,
)
