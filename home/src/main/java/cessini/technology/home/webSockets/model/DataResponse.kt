package cessini.technology.home.webSockets.model

import com.google.gson.annotations.SerializedName

data class DataResponse(
    @SerializedName("__v") val __v: Int,
    @SerializedName("_id") val _id: String,
    @SerializedName("admin") val admin: String,
    @SerializedName("adminSocket") val adminSocket: String,
    @SerializedName("allowedUsers") val allowedUsers: List<AllowedUserResponse>,
    @SerializedName("chat") val chat: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("description") val description: String,
    @SerializedName("live") val live: Boolean,
    @SerializedName("messages") val messages: List<MessageResponse>,
    @SerializedName("otherUsers") val otherUsers: List<Any>,
    @SerializedName("room") val room: String,
    @SerializedName("sub_category") val sub_category: List<String>,
    @SerializedName("title") val title: String
)