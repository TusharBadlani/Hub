package cessini.technology.newrepository.websocket.video.model.response

import cessini.technology.newapi.model.ApiComment
import cessini.technology.newapi.services.story.model.response.ApiViewAndDuration
import com.google.gson.annotations.SerializedName

data class ApiVideoLikeCommentAndView(
    @SerializedName(value = "video_id") val id: String,
    val likes: Int,
    val comments: ApiComments,
    val views: ApiViewAndDuration,
)

data class ApiComments(
    @SerializedName(value = "_id") val id: String,
    @SerializedName(value = "video_id") val videoId: String,
    val comments: List<ApiComment>,
)
