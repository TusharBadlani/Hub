package cessini.technology.newrepository.mappers

import cessini.technology.model.ViewAndDuration
import cessini.technology.newapi.services.story.model.response.ApiViewAndDuration

fun ApiViewAndDuration.toModel() = ViewAndDuration(
    views = views,
    duration = duration,
)
