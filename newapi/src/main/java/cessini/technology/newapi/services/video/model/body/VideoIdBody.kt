package cessini.technology.newapi.services.video.model.body

import com.google.gson.annotations.SerializedName

data class VideoIdBody(
    @SerializedName(value = "video_id") val id: String,
)
