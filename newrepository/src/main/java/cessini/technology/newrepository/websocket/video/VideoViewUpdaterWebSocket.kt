package cessini.technology.newrepository.websocket.video

import android.util.Log
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.websocket.WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.*
import javax.inject.Inject

class VideoViewUpdaterWebSocket @Inject constructor(
    authPreferences: AuthPreferences,
    userIdentifierPreferences: UserIdentifierPreferences,
) {
    private val userId = userIdentifierPreferences.id

    private val trackSocket = OkHttpClient().newWebSocket(
        request = Request.Builder()
            .header(name = "Authorization", value = "Bearer ${authPreferences.accessToken}")
            .url(VIDEO_VIEW_UPDATER_SOCKET_URL).build(),
        listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("http", "socket open")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("http", "socket closed")
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("http", "socket $text")
            }
        },
    )

    operator fun invoke(videoId: String, duration: Int) {
        trackSocket.send(Gson().toJson(SocketBody(userId, videoId, duration + 10)))
    }

    fun close() {
        trackSocket.close(NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    companion object {
        private const val VIDEO_VIEW_UPDATER_SOCKET_URL = "ws://65.2.30.225/ws/vt"
    }
}

private data class SocketBody(
    @SerializedName("id") val id: String,
    @SerializedName("video_id") val videoId: String,
    @SerializedName("duration") val duration: Int,
)
