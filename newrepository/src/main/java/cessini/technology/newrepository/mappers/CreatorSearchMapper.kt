package cessini.technology.newrepository.mappers

import cessini.technology.model.search.CreatorSearch
import cessini.technology.newrepository.websocket.search.response.CreatorSearchResponse

fun CreatorSearchResponse.toModel() = CreatorSearch(
    id = id,
    name = name,
    channelName = channelName,
    profilePicture = profilePicture,
)
