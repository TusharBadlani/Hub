package cessini.technology.newrepository.mappers

import cessini.technology.model.ProfileMedia
import cessini.technology.newapi.services.myworld.model.response.ApiProfileMedia

fun ApiProfileMedia.toModel() = ProfileMedia(
    videos = data?.videos?.map { it.toModel() },
    viewers = data?.stories?.map {
        it.toModel()
    },
    rooms = data?.rooms?.map { it.toModel() },
)
