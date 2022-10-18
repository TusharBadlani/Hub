package cessini.technology.newrepository.websocket.video

import cessini.technology.model.VideoLikeCommentAndView
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.websocket.WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE
import cessini.technology.newrepository.websocket.video.model.response.ApiVideoLikeCommentAndView
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class VideoLikeViewAndCommentTrackWebSocket(private val block: (VideoLikeCommentAndView) -> Unit) {
    private val trackSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(VIDEO_LIKE_VIEW_COMMENT_TRACK_SOCKET_URL).build(),
        listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Gson().fromJson(text, ApiVideoLikeCommentAndView::class.java)
                    .toModel().run(block)
            }
        },
    )

    fun close() {
        trackSocket.close(NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

    companion object {
        private const val VIDEO_LIKE_VIEW_COMMENT_TRACK_SOCKET_URL =
            "ws://34.207.62.211:8000/ws/video"
    }
}
