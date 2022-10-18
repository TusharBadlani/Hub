package cessini.technology.model.search

import cessini.technology.model.Room

sealed class SearchResult {
    class MySpaceResult(val myspace:List<Room>):SearchResult()
    class CreatorResult(val creators: List<CreatorSearch>): SearchResult()
    class VideoResult(val videos: List<VideoSearch>): SearchResult()
}
