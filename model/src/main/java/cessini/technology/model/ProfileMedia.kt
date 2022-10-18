package cessini.technology.model

data class ProfileMedia(
    val videos: List<Video>?=null,
    val viewers: List<Viewer>?=null,
    val rooms: List<Room>?=null,
)