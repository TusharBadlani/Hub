package cessini.technology.home.webSockets.model

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("created_at") val created_at: String,
    @SerializedName("message") val message: String,
    @SerializedName("name") val name: String,
    @SerializedName("profile_picture") val profile_picture: String,
    @SerializedName("user_id") val user_id: String
)