package cessini.technology.newrepository.mappers

import cessini.technology.model.Video
import cessini.technology.model.VideoProfile
import cessini.technology.newapi.services.video.model.response.ApiVideo
import cessini.technology.newapi.services.video.model.response.ApiVideoProfile

fun ApiVideo.toModel() = Video(
    id = id,
    profile = profile.toModel(),
    title = title,
    description = description,
    thumbnail = thumbnail,
    duration = duration.toIntOrNull() ?: 0,
    category = category,
    url = url,
    timestamp = timestamp,
    viewCount = viewCount.totalViewers ?: 0,
    roomCount = mySpace.size
)

fun ApiVideoProfile.toModel() = VideoProfile(
    id = id,
    name = name,
    channel = channel,
    picture = picture,
)
