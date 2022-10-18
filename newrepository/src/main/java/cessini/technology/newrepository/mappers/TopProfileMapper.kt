package cessini.technology.newrepository.mappers

import cessini.technology.model.TopProfile
import cessini.technology.newapi.services.explore.model.response.ApiTopProfile

fun ApiTopProfile.toModel() = TopProfile(
    id = id,
    name = name,
    channelName = channelName,
    profilePicture = profilePicture,
    is_following = is_following
)
