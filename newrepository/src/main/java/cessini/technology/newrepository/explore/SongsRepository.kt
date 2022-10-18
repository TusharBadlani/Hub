package cessini.technology.newrepository.explore

import cessini.technology.model.Audio
import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.services.explore.ExploreService
import cessini.technology.newrepository.mappers.toAudio
import javax.inject.Inject

class SongsRepository @Inject constructor(
    private val exploreApi: ExploreService,
) {
    suspend fun getSongs(): ArrayList<Audio> {
        return exploreApi.getSongs().getOrThrow().message.filter { it.value.isNotEmpty() }.toAudio()
    }
}
