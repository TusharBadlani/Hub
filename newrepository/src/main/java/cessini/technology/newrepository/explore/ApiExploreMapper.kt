package cessini.technology.newrepository.explore

import cessini.technology.model.Explore
import cessini.technology.newapi.services.explore.model.response.ApiExplore
import cessini.technology.newrepository.mappers.toModel

fun ApiExplore.toModel(): Explore {
    return message?.run {
        Explore(
            publicEvents = publicEvents.map { it.toModel() },
            topProfiles = topProfiles.map { it.toModel() },
            rooms = rooms.map { it.toModel() },
//            liveRooms = listOf(LiveRoom(null,"1",null,null,null,null,null,null,null,"jbh","vgc","ugftrd","kjhbhf",null,"hyg",
//                emptyList())),
            liveRooms = live_rooms,
//            videos = videos.map {
//                Pair(it.key, it.value.map { video -> video.toModel() })
//            }
        )
    } ?: Explore()
}
