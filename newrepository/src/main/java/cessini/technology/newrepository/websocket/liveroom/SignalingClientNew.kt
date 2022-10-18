package cessini.technology.newrepository.websocket.liveroom


import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SignalingClientNew private constructor() {
    private lateinit var socket: Socket
    private val room = "OldPlace"
    private lateinit var callback: Callback
    private val trustAll = arrayOf<TrustManager>(
        object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    )
    fun init(callback: Callback) {
        this.callback = callback
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAll, null)
            IO.setDefaultHostnameVerifier { hostname: String?, session: SSLSession? -> true }
            IO.setDefaultSSLContext(sslContext)
            // STUN TURN IP added
            socket = IO.socket("https://13.233.150.222:3478")
            socket.connect()
            socket.emit("create or join", room)
            socket.on("created", Emitter.Listener { args: Array<Any?>? ->
                Log.e("chao", "room created:" + socket.id())
                callback.onCreateRoom()
            })
            socket.on("full",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.e(
                        "chao",
                        "room full"
                    )
                })
            socket.on("join", Emitter.Listener { args: Array<Any> ->
                Log.e("chao", "peer joined " + Arrays.toString(args))
                callback.onPeerJoined(args[1].toString())
            })
            socket.on("joined", Emitter.Listener { args: Array<Any?>? ->
                Log.e("chao", "self joined:" + socket.id())
                callback.onSelfJoined()
            })
            socket.on("log",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.e(
                        "chao",
                        "log call " + Arrays.toString(args)
                    )
                })
            socket.on("bye", Emitter.Listener { args: Array<Any> ->
                Log.e("chao", "bye " + args[0])
                callback.onPeerLeave(args[0] as String)
            })
            socket.on("message", Emitter.Listener { args: Array<Any?> ->
                Log.e("chao", "message " + Arrays.toString(args))
                val arg = args[0]
                if (arg is String) {
                } else if (arg is JSONObject) {
                    val data = arg
                    val type = data.optString("type")
                    if ("offer" == type) {
                        callback.onOfferReceived(data)
                    } else if ("answer" == type) {
                        callback.onAnswerReceived(data)
                    } else if ("candidate" == type) {
                        callback.onIceCandidateReceived(data)
                    }
                }
            })
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        socket!!.emit("bye", socket!!.id())
        socket!!.disconnect()
        socket!!.close()
        instance = null
    }

    fun sendIceCandidate(iceCandidate: IceCandidate, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            jo.put("from", socket!!.id())
            jo.put("to", to)
            socket!!.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendSessionDescription(sdp: SessionDescription, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            jo.put("from", socket!!.id())
            jo.put("to", to)
            socket!!.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    interface Callback {
        fun onCreateRoom()
        fun onPeerJoined(socketId: String)
        fun onSelfJoined()
        fun onPeerLeave(msg: String)
        fun onOfferReceived(data: JSONObject)
        fun onAnswerReceived(data: JSONObject)
        fun onIceCandidateReceived(data: JSONObject)
    }

    companion object {
        private var instance: SignalingClientNew? = null
        fun get(): SignalingClientNew? {
            if (instance == null) {
                synchronized(SignalingClient::class.java) {
                    if (instance == null) {
                        instance = SignalingClientNew()
                    }
                }
            }
            return instance
        }
    }
}
