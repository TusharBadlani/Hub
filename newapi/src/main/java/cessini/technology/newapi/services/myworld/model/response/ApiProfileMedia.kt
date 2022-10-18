package cessini.technology.newapi.services.myworld.model.response

import cessini.technology.newapi.services.myspace.model.response.ApiRoom
import cessini.technology.newapi.services.story.model.response.ApiStory
import cessini.technology.newapi.services.video.model.response.ApiVideo

data class ApiProfileMedia(
    val data: ApiProfileMediaData?=null,
)

data class ApiProfileMediaData(
    val videos: List<ApiVideo>?=null,
    val rooms: List<ApiRoom>?=null,
    val stories: List<ApiStory>?=null,
)
