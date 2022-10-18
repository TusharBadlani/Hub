package cessini.technology.newrepository.mappers

import cessini.technology.model.PublicEvent
import cessini.technology.newapi.services.explore.model.response.ApiPublicEvent

fun ApiPublicEvent.toModel() = PublicEvent(
    id = id,
    image = image,
    title = title,
    description = description
)
