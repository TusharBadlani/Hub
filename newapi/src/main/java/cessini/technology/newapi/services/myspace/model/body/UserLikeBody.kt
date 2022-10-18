package cessini.technology.newapi.services.myspace.model.body

import com.google.gson.annotations.SerializedName

data class UserLikeBody(
    @SerializedName(value = "user_id")val user_id: String,
    @SerializedName(value = "room_code") val roomName: String,
)
