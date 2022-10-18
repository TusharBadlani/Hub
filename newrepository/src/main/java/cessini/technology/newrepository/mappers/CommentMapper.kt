package cessini.technology.newrepository.mappers

import cessini.technology.model.Comment
import cessini.technology.newapi.model.ApiComment

fun ApiComment.toModel() = Comment(
    id = id,
    profileId = profileId,
    channelName = channelName,
    profilePicture = profilePicture,
    text = text,
)
