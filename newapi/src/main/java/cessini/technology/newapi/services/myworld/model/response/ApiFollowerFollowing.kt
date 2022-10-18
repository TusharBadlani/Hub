package cessini.technology.newapi.services.myworld.model.response

import com.google.gson.annotations.SerializedName

data class ApiFollowerFollowing(
    val message: String,
    val status: Boolean,
    val data: List<ApiUser> = emptyList(),
)

data class ApiUser(
    @SerializedName(value = "id") val id: String,
    val name: String,
    val email: String,
    @SerializedName(value = "channel_name") val channelName: String,
    @SerializedName(value = "profile_picture") val profilePicture: String,
)
