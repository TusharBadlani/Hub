package cessini.technology.newrepository.websocket.video

import android.util.Log
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.websocket.WebSocketConstants
import com.google.gson.Gson
import okhttp3.*
import javax.inject.Inject

class RoomViewerUpdaterWebSocket @Inject constructor(
    authPreferences: AuthPreferences,
    userIdentifierPreferences: UserIdentifierPreferences
) {
    companion object {
        private const val TAG = "RoomViewerSocket"
        private const val ROOM_VIEWER_UPDATER_SOCKET_URL = "ws://3.108.54.21/ws/vt"
    }

    private val userId = userIdentifierPreferences.id
    private val roomViewerTrackSocket = OkHttpClient().newWebSocket(
        request = Request.Builder()
            .header(name = "Authorization", value = "Bearer ${authPreferences.accessToken}")
            .url(ROOM_VIEWER_UPDATER_SOCKET_URL).build(),
        listener = object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "socket open")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "socket closed")
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "socket $text")
            }
        }

    )

    operator fun invoke( room_code: String,duration: Int){
        roomViewerTrackSocket.send(Gson().toJson(RoomViewerSocketBody(userId,room_code,duration)))
    }
    fun close(){
        roomViewerTrackSocket.close(WebSocketConstants.NORMAL_CLOSURE_STATUS_CODE, reason = null)
    }

}

private data class RoomViewerSocketBody(
    val id: String,
    val room_code: String,
    val duration: Int
)