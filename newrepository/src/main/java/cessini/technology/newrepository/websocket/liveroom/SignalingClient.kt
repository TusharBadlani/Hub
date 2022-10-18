package cessini.technology.newrepository.websocket.liveroom

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class SignalingClient private constructor(){

    // socket for creating or joining the room
    private val createRoomSocket = "ws://3.109.143.89/ws/room/"

    // socket for sending the data to other peers in the room
    private val messageRoomSocket = "ws://3.109.143.89/ws/message/"

    // this callback is used to acknowledge the activity about receiving events from other peers from socket
    var listener: SignalingClientListener? = null

    // initializing the createRoomSocket and listener to the events
    private val createRoom = OkHttpClient().newWebSocket(
        request = Request.Builder().url(createRoomSocket).build(),
        listener = object: WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                callback?.onConnectionEstablished(json.optString("id"), json.optString("room_id"))
            }
        }
    )

    // initializing the messageRoom socket and listener to the events
    private val messageRoom = OkHttpClient().newWebSocket(
        request = Request.Builder().url(messageRoomSocket).build(),
        listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                when(json.optString("type")) {
                    // when someone joins the room it creates an offer
                    "offer" -> {
                        callback?.onOfferReceived(json)
                    }
                    // when other peers accept your offer they return answer
                    "answer" -> {
                        callback?.onAnswerReceived(json)
                    }
                    // candidate helps us to get their computer/phone related
                    // details which is useful for establishing/maintaining connection
                    "candidate" -> {
                        callback?.onIceCandidateReceived(json)
                    }
                    // this is trigger when someone toggles it's mic
                    // this is used to animate user's view
                    "mic" -> {
                        callback?.toggleMicState(json)
                    }
                }
            }
        }
    )

    // when you join the room this is invoked and sends your sdp
    fun sendSessionDescription(sdp: SessionDescription, toId: String, fromId: String) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            jo.put("from", fromId)
            jo.put("to", toId)
            messageRoom.send(jo.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // when your ice candidates change this is used to send your ice candidate
    // to other users in the room to maintain connection
    fun sendIceCandidate(iceCandidate: IceCandidate, toId: String, fromId: String) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            jo.put("from", fromId)
            jo.put("to", toId)
            messageRoom.send(jo.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // when you toggle your mic this is called which is used to animate user
    fun sendToggleMicState(fromId: String, toId: String, isMicOn: Boolean) {
        val jo = JSONObject()
        try {
            jo.put("type","mic")
            jo.put("isOn",isMicOn)
            jo.put("from",fromId)
            jo.put("to",toId)
            messageRoom.send(jo.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // this is called when you join the room which acknowledges other peers in the room
    fun createOrJoinRoom(id: String, roomId: String) {
        val json = JSONObject()
        json.put("user_id",id)
        json.put("room_id",roomId)
        createRoom.send(json.toString())
    }

    // this is called which maintains singleton pattern and initializes callbacks
    companion object {
        private var callback: SignalingClientListener? = null
        private var instance: SignalingClient? = null
        fun get(signalingClientListener: SignalingClientListener): SignalingClient? {
            if (instance == null) {
                synchronized(SignalingClient::class.java) {
                    if (instance == null) {
                        instance = SignalingClient()
                    }
                }
            }
            if(callback == null){
                callback = signalingClientListener
            }
            return instance
        }
    }
}

// this is the callback interface which is implemented by activity
interface SignalingClientListener {
    fun onConnectionEstablished(id: String, roomId: String)
    fun onOfferReceived(data: JSONObject)
    fun onAnswerReceived(data: JSONObject)
    fun onIceCandidateReceived(data: JSONObject)
    fun onCallEnded()
    fun toggleMicState(data: JSONObject)
}
