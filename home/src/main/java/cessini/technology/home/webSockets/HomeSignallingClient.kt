package cessini.technology.home.webSockets

import android.util.Log
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.coroutines.coroutineContext

class HomeSignallingClient() {
    private val TAG = "HomeSignallingClient"
    private lateinit var mSocket:Socket
    private lateinit var callBack:SocketEventCallback
    private val SIGNALLING_CLIENT_BASE_URL = "https://socket.joinmyworld.in"

    fun requestJoinRoom(callback: SocketEventCallback, data: JSONObject) {
        this.callBack = callback
        try {
            mSocket = IO.socket(SIGNALLING_CLIENT_BASE_URL)
            mSocket.connect()

            if(mSocket.isActive) Log.d(TAG, "Connection is active ${mSocket.isActive}")
            Log.d(TAG, "Inside requestJoinRoom()")

            mSocket.emit("permission", data)

            mSocket.on("no permission required", Emitter.Listener { args: Array<out Any>? ->
                Log.d(TAG, "no permission required ${args?.get(1).toString()}")
                mSocket.emit("join room", data)
            })

            mSocket.on("all other users", Emitter.Listener { args: Array<out Any>? ->
                Log.d(TAG, "all other users: ${args?.get(1).toString()}")
                args?.get(1)?.let { callback.onJoinRequestAccepted(it.toString()) }
            })

            mSocket.on("allowed", Emitter.Listener { args: Array<out Any>? ->
                Log.d(TAG, "allowed: ${args?.get(1).toString()}")
                mSocket.emit("join room", data)
            })

            mSocket.on("denied", Emitter.Listener { args: Array<out Any>? ->
                Log.d(TAG, "denied ${args?.get(1).toString()}")
                args?.get(1)?.let { callback.onJoinRequestDenied(it.toString()) }
            })

        } catch (e:URISyntaxException) {
            Log.e(TAG, e.message.toString())
            mSocket.disconnect()
        }
    }

    /*interface Callback {
        fun onJoinRequestAccepted(socketId: String)
        fun onJoinRequestDenied(msg: String)
    }*/
}