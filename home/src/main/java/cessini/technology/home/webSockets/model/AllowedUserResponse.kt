package cessini.technology.home.webSockets.model

import com.google.gson.annotations.SerializedName

data class AllowedUserResponse(
    @SerializedName("channelName") val channelName: String,
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("profilePicture") val profilePicture: String,
    @SerializedName("socker") val socker: String
)