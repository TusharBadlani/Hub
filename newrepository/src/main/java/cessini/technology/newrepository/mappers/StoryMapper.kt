package cessini.technology.newrepository.mappers

import cessini.technology.model.Viewer
import cessini.technology.newapi.services.story.model.response.ApiStory

fun ApiStory.toModel() = Viewer(
    id = id,
    profileId = profileId,
    caption = caption,
    thumbnail = thumbnail,
    duration = duration.toIntOrNull() ?: 0,
    url = url,
    timestamp = timestamp,
)
