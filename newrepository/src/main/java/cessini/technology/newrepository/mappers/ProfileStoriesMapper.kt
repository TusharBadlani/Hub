package cessini.technology.newrepository.mappers

import cessini.technology.model.ProfileStories
import cessini.technology.newapi.services.story.model.response.ApiProfileStories

fun ApiProfileStories.toModel() = ProfileStories(
    profileId = profileId,
    picture = picture,
    viewers = stories.map { it.toModel() }
)
