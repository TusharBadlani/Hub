package cessini.technology.newrepository.websocket.story

import cessini.technology.model.StoryLikeAndView
import cessini.technology.newrepository.toModel
import cessini.technology.newrepository.websocket.WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE
import cessini.technology.newrepository.websocket.video.model.response.ApiStoryLikeAndView
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StoryViewAndLikeTrackWebSocket(private val block: (StoryLikeAndView) -> Unit) {
    private val trackSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(STORY_VIEW_AND_LIKE_TRACK_SOCKET_URL).build(),
        listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Gson().fromJson(text, ApiStoryLikeAndView::class.java)
                    .toModel().run(block)
            }
        }
    )

    fun close() {
        trackSocket.close(NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    companion object {
        private const val STORY_VIEW_AND_LIKE_TRACK_SOCKET_URL =
            "ws://52.90.106.57:8000/ws/viewers"
    }
}
