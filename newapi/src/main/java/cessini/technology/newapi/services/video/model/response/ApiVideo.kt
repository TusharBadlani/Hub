package cessini.technology.newapi.services.video.model.response

import com.google.gson.annotations.SerializedName

data class ApiVideoDetail(
    val data: ApiVideo = ApiVideo(),
)

data class ApiVideo(
    @SerializedName(value = "_id") val id: String = "",
    val profile: ApiVideoProfile = ApiVideoProfile(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val duration: String = "",
    val category: List<String> = emptyList(),
    @SerializedName(value = "upload_file") val url: String = "",
    val timestamp: Float = 0F,
    @SerializedName("total_viewers") val viewCount: TotalViewers = TotalViewers(),
    @SerializedName("myspace") val mySpace: List<TimeLineMySpace> = emptyList()
)

data class TimeLineMySpace(
    @SerializedName("_id") val id: String,
    val title: String,
    val schedule: String,
    val private: Boolean,
    val live: Boolean
)

data class TotalViewers(
    @SerializedName("_id")val videoId: String? = "",
    @SerializedName("total_viewers") val totalViewers: Int? = 0
)

data class ApiVideoProfile(
    @SerializedName(value = "_id") val id: String = "",
    val name: String = "",
    @SerializedName(value = "channel_name") val channel: String = "",
    @SerializedName(value = "profile_picture") val picture: String = "",
)
