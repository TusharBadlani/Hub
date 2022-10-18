package cessini.technology.cvo.homemodels.socket

import com.google.gson.annotations.SerializedName

data class Profile(
    val id: String,
    val name: String,
    @SerializedName("channel_name") val channelName: String?,
    @SerializedName("profile_picture") val profilePicture: String?,
)
