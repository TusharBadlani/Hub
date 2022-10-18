package cessini.technology.newrepository.mappers

import cessini.technology.model.Audio
import cessini.technology.model.Song
import cessini.technology.model.SongInfo
import cessini.technology.newapi.services.explore.model.response.ApiSong

fun ApiSong.toModel() = Song(
    id = id,
    title = title,
    thumbnail = thumbnail,
    duration = duration.toInt(),
    url = url,
    category = category,
)

fun Song.toSongInfo() = SongInfo(
    id = id,
    title = title,
    thumbnail = thumbnail,
    duration = duration,
    upload_file = url,
)

fun Map<String, List<ApiSong>>.toAudio(): ArrayList<Audio> {
    return map {
        Audio(
            id = it.value.hashCode(),
            category = it.key,
            uploads = it.value.map { song -> song.toModel().toSongInfo() }.run { ArrayList(this) }
        )
    }.run { ArrayList(this) }
}
