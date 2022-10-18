package cessini.technology.newrepository.mappers

import cessini.technology.model.CreateRoomRequest
import cessini.technology.model.Creator
import cessini.technology.model.Listener
import cessini.technology.model.Room
import cessini.technology.newapi.services.myspace.model.response.ApiCreator
import cessini.technology.newapi.services.myspace.model.response.ApiListeners
import cessini.technology.newapi.services.myspace.model.response.ApiRoom
import cessini.technology.newapi.services.myworld.model.response.ApiCreateRoomMessage
import cessini.technology.newapi.services.myworld.model.response.ApiRoomMessages

fun ApiRoom.toModel() = Room(
    id = id,
    name = code,
    title = title,
    creator = creator.toModel(),
    time = schedule,
    private = private,
    live = live,
    listeners = listeners.map { it.toModel() },
    categories = category
)

fun ApiListeners.toModel() = Listener(
    _id = profileId,
    name = profileName,
    profile_picture = profilePicture,
    channel_name = profileChannelName,
)

fun ApiCreator.toModel() = Creator(
    id = id,
    name = name,
    profilePicture = profilePicture,
    channelName = channelName,
)

fun ApiRoomMessages.toModels() = CreateRoomRequest(
    userId = id,
    userName = userName,
    profileImage = userProfileImage,
    message = message
)