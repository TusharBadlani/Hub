package cessini.technology.newrepository.websocket.search.response

import com.google.gson.annotations.SerializedName

data class VideoSearchResponse(
    @SerializedName(value = "_id") val id: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val category: List<String>,
    @SerializedName(value = "creator_name") val creatorName: String,
    @SerializedName(value = "channel_name") val channelName: String,
)
