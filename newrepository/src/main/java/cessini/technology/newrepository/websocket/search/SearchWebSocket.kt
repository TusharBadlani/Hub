package cessini.technology.newrepository.websocket.search

import android.util.Log
import cessini.technology.model.search.*
import cessini.technology.newapi.services.myspace.model.response.ApiRoom
import cessini.technology.newapi.services.video.model.response.ApiVideo
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.websocket.WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE
import cessini.technology.newrepository.websocket.search.response.CreatorSearchResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SearchWebSocket(private val block: (SearchResult) -> Unit) {
    private var currentSearchType = SearchType.creator

    private val searchSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(SEARCH_SOCKET_URL).build(),
        listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                when (currentSearchType) {
                    SearchType.myspace -> roomsFromJson(text)
                    SearchType.creator -> creatorsFromJson(text)
                    SearchType.video -> videosFromJson(text)
                }.run(block)
            }
        }
    )

    fun send(query: String, searchType: SearchType) {
        currentSearchType = searchType
        //Log.e("SearchSend","SearchWebSocket search called")
        searchSocket.send(Gson().toJson(SearchBody(searchType, query)))
    }

    fun close() {
        searchSocket.close(NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    companion object {
        // TODO: Replace with wss://others-api.joinmyworld.live/ws/search
        private const val SEARCH_SOCKET_URL = "ws://15.207.114.134/ws/search"
    }
}

private fun creatorsFromJson(json: String): SearchResult.CreatorResult {
    return Gson().fromJson<List<CreatorSearchResponse>>(
        json,
        object : TypeToken<List<CreatorSearchResponse>>() {}.type,
    ).map { it.toModel() }.run(SearchResult::CreatorResult)
}

private fun videosFromJson(json: String): SearchResult.VideoResult {
    return Gson().fromJson<List<ApiVideo>>(
        json,
        object : TypeToken<List<ApiVideo>>() {}.type,
    ).map { it.toVideoSearch() }.run(SearchResult::VideoResult)
}
private fun roomsFromJson(json: String): SearchResult.MySpaceResult {
    return Gson().fromJson<List<ApiRoom>>(
        json,
        object : TypeToken<List<ApiRoom>>() {}.type,
    ).map { it.toModel() }.run(SearchResult::MySpaceResult)
}

private data class SearchBody(
    val type: SearchType,
    val keyword: String
)

private fun ApiVideo.toVideoSearch() = VideoSearch(
    id = id,
    title = title,
    description = description,
    category = category,
    thumbnail = thumbnail,
    creatorName = profile.name,
    channelName = profile.channel,
)