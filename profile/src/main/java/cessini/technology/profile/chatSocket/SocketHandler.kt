package cessini.technology.profile.chatSocket

import io.socket.client.IO
import io.socket.client.Socket

object SocketHandler {

    private const val TAG = "SocketHandler"

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        // TODO: Replace with https://messaging-api.joinmyworld.live
        mSocket = IO.socket("http://13.127.77.21")
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