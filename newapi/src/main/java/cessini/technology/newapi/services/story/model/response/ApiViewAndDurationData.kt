package cessini.technology.newapi.services.story.model.response

import com.google.gson.annotations.SerializedName

data class ApiViewAndDurationData(
    val data: ApiViewAndDuration = ApiViewAndDuration(),
)

data class ApiViewAndDuration(
    @SerializedName(value = "total_duration") val duration: Int = 0,
    @SerializedName(value = "total_viewers") val views: Int = 0,
)
