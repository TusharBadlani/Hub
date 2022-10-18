package cessini.technology.newrepository.websocket.video.model.response

import cessini.technology.newapi.services.story.model.response.ApiViewAndDuration
import com.google.gson.annotations.SerializedName

data class ApiStoryLikeAndView(
    @SerializedName(value = "story_id") val id: String,
    val likes: Int,
    val viewAndDuration: ApiViewAndDuration,
)
