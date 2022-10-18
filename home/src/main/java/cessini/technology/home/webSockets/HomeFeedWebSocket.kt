package cessini.technology.home.webSockets

import android.util.Log
import cessini.technology.home.webSockets.model.HomeFeedSocketPayload
import cessini.technology.home.webSockets.model.HomeFeedSocketResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*


class HomeFeedWebSocket(private val block : (HomeFeedSocketResponse)->Unit) {


    companion object {
        private const val TAG = "HomeFeedWedSocket"
        private const val CLOSE_STATUS = 1000
        private const val HOME_FEED_SOCKET_URL = "ws://timeline.joinmyworld.in/ws/live"
    }

    private val homeFeedSocket = OkHttpClient().newWebSocket(
        request = Request.Builder().url(HOME_FEED_SOCKET_URL).build(),
        listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "onOpen: ${response.body}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "onMessage: $text")
                getHomeFeedWebSocketResponse(text).run(block)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "onClosed: +$reason")
            }
        }
    )

    fun send(homeFeedSocketPayload: HomeFeedSocketPayload) {
        homeFeedSocket.send(Gson().toJson(homeFeedSocketPayload))
    }

    fun close(reason:String) {
        homeFeedSocket.close(CLOSE_STATUS, reason)
    }


}
private fun getHomeFeedWebSocketResponse(response:String):HomeFeedSocketResponse {
    return Gson().fromJson(
        response,
        object : TypeToken<HomeFeedSocketResponse>() {}.type,
    )
}