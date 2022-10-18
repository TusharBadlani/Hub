package cessini.technology.newrepository.mappers

import cessini.technology.model.VideoCategory
import cessini.technology.newapi.services.explore.model.response.ApiVideoCategoryData

fun ApiVideoCategoryData.toModel() = VideoCategory(
    id = id,
    name = name,
)
