package cessini.technology.newapi.services.story.model.response

import com.google.gson.annotations.SerializedName

data class ApiGetStories(
    val data: List<ApiProfileStories>,
)

data class ApiProfileStories(
    @SerializedName(value = "profile") val profileId: String,
    @SerializedName(value = "profile_picture") val picture: String,
    @SerializedName(value = "data") val stories: List<ApiStory>,
)

data class ApiStory(
    @SerializedName(value = "_id") val id: String,
    @SerializedName(value = "profile_id") val profileId: String,
    val caption: String,
    val thumbnail: String,
    val duration: String,
    @SerializedName(value = "upload_file") val url: String,
    val timestamp: Float,
)
