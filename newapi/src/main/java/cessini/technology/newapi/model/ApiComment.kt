package cessini.technology.newapi.model

import com.google.gson.annotations.SerializedName

data class ApiComment(
    @SerializedName(value = "comment_id") val id: String,
    @SerializedName(value = "profile_id") val profileId: String,
    @SerializedName(value = "channel_name") val channelName: String,
    @SerializedName(value = "profile_picture") val profilePicture: String,
    @SerializedName(value = "comment") val text: String,
)
