package cessini.technology.newrepository.mappers

import cessini.technology.model.RequestProfile
import cessini.technology.newapi.services.myspace.model.response.ApiRequestProfile

fun ApiRequestProfile.toModel() = RequestProfile(
    id = id,
    name = name,
    channel = channel,
    picture = picture,
)
