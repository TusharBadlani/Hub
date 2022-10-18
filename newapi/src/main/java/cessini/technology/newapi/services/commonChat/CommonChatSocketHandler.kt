package cessini.technology.newapi.services.commonChat

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object CommonChatSocketHandler {

    lateinit var mSocket: Socket
    const val TAG = "CommonChatSocketHandler"
    @Synchronized
    fun setSocket(roomID: String) {
        try {
            val param : IO.Options = IO.Options()
            param.query = "room=$roomID"
            mSocket = IO.socket(CommonChatConstants.BASE_ENDPOINT,param)
        } catch (e: URISyntaxException) {
                Log.d(TAG,e.localizedMessage,e)
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }


}