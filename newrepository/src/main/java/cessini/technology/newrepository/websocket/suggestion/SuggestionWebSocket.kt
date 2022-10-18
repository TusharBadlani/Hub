package cessini.technology.newrepository.websocket.suggestion

import cessini.technology.model.Video
import cessini.technology.newapi.services.video.model.response.ApiVideo
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.websocket.WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SuggestionWebSocket(private val block: (List<Video>) -> Unit) {
    private val suggestionSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(SUGGESTION_SOCKET_URL).build(),
        listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Gson().fromJson(text, SocketResponse::class.java).data
                    .map { it.toModel() }.run(block)
            }
        }
    )

    fun suggest(videoId: String) {
        suggestionSocket.send(text = "{\"id\":\"$videoId\"}")
    }

    fun close() {
        suggestionSocket.close(NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    companion object {
        private const val SUGGESTION_SOCKET_URL = "ws://52.66.250.212/ws/sugs/"
//        private const val SUGGESTION_SOCKET_URL = "ws://15.207.54.146/ws/sugs/"
    }
}

data class SocketResponse(
    val data: List<ApiVideo>,
)
