package cessini.technology.newrepository.mappers

import cessini.technology.model.search.VideoSearch
import cessini.technology.newrepository.websocket.search.response.VideoSearchResponse

fun VideoSearchResponse.toModel() = VideoSearch(
    id = id,
    title = title,
    description = description,
    category = category,
    thumbnail = thumbnail,
    creatorName = creatorName,
    channelName = channelName,
)
