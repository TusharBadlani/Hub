package cessini.technology.newrepository.websocket.timeline

import android.util.Log
import cessini.technology.model.Video
import cessini.technology.newapi.services.video.model.response.ApiVideo
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.websocket.WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class TimelineWebSocket(deviceId: String, private val block: (List<Video>) -> Unit) {
    private val timelineSocketUrl = "ws://52.66.250.212/ws/feeds/?devId=$deviceId"

    private val timelineSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(timelineSocketUrl).build(),
        listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                next()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                with(Gson().fromJson(text, SocketResponse::class.java).data.map { it.toModel() }) {
                    let(block)
                }
            }
        },
    )

    fun close() {
        timelineSocket.close(NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    fun next() {
        timelineSocket.send("""{"next": true}""")
    }
}

private data class SocketResponse(
    val data: List<ApiVideo>,
)
