package cessini.technology.newrepository.mappers

import cessini.technology.model.User
import cessini.technology.newapi.services.myworld.model.response.ApiUser

fun ApiUser.toModel() = User(
    id = id,
    name = name,
    email = email,
    channelName = channelName,
    profilePicture = profilePicture,
)
