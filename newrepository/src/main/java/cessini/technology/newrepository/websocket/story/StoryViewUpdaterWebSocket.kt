package cessini.technology.newrepository.websocket.story

import cessini.technology.newrepository.websocket.WebSocketConstants
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener


class StoryViewUpdaterWebSocket {
    private val trackSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(STORY_VIEW_UPDATER_SOCKET_URL).build(),
        listener = object : WebSocketListener() {},
    )

    fun send(userId: String, storyId: String, duration: Int) {
        trackSocket.send(Gson().toJson(SocketBody(userId, storyId, duration)))
    }

    fun close() {
        trackSocket.close(WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    companion object {
        private const val STORY_VIEW_UPDATER_SOCKET_URL = "ws://52.90.106.57:8000/ws/vt"
    }
}

private data class SocketBody(
    val id: String,
    @SerializedName(value = "story_id") val videoId: String,
    val duration: Int,
)

