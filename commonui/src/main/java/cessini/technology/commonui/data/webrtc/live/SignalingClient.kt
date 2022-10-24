package cessini.technology.commonui.data.webrtc.live

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

class SignalingClient() {
    var rname = "LiveMySpaceActivity"
    private lateinit var socket: Socket

    private lateinit var callback: Callback
    var lastConnection = ""

    // TODO: REPLACE WITH "https://rooms-api.joinmyworld.live"
    private val socketUrl = "http://65.1.147.5/"

    private val hostnameVerifier: HostnameVerifier = HostnameVerifier { hostname, session -> true }
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })

    val trustManager = trustAllCerts[0] as X509TrustManager
    fun init(callback: Callback, rname: String) {
        this.callback = callback
        try {

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, null)

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build()
// default settings for all sockets
//            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
//            IO.setDefaultOkHttpCallFactory(okHttpClient);

            // set as an option
            val opts = IO.Options()
            socket = IO.socket(socketUrl, opts)
//            Log.d("json",roomJson)
            val data = JSONObject("""{"room":"$rname"}""")
            socket.emit("create or join", data)
            socket.on("created", Emitter.Listener { args: Array<Any?>? ->
                Log.e("chao", "room created:" + args.toString() + " sid- ${socket.id()}")
                callback.onCreateRoom(socket.id())
            })
            Log.d("Working", "one")

            socket.on("full",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.e(
                        "chao",
                        "room full"
                    )
                })

            try {
                Log.d("id", socket.id().toString())
            } catch (e: Exception) {
                Log.d("id", e.toString())
            }

            socket.on("join", Emitter.Listener { args: Array<Any>? ->

                Log.e("joined ", "data= ${Arrays.toString(args)} socket id= ${socket.id()}")
                val data = args!![0] as JSONObject
                Log.e("sid", data["sid"].toString())
                callback.onPeerJoined(data["sid"].toString())

            }
            )

            Log.d("Working", "second")
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

            Log.d("SHIVAM3", "ASJ")
            socket.on("bye", Emitter.Listener { args: Array<Any> ->
                Log.e("chao", "bye " + args[0])
                callback.onPeerLeave(args[0] as String)
            })

            Log.d("Working", "third")
            socket.on("message response") { args ->
                Log.e("chao arg", "message " + Arrays.toString(args))

                val arg = args[0]
                Log.d("arg", arg.toString())
                if (arg is String) {
                } else if (arg is JSONObject) {
                    var data = arg
                    Log.e("data", data.javaClass.name)
                    try {
                        val type = data.optString("type")
                        if ("offer" == type) {
                            callback.onOfferReceived(data)
                        } else if ("answer" == type) {
                            callback.onAnswerReceived(data)
                        } else
                            callback.onIceCandidateReceived(data)
                    } catch (e: Exception) {
                        Log.e("exception", "$e ")
                    }
                }

            }

            //Other Methods
            socket.connect()
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: KeyManagementException) {
            throw RuntimeException(e)
        }
    }

    fun destroy() {
        socket.disconnect()
        socket.close()
//        instance = null
    }

    fun sendIceCandidate(iceCandidate: IceCandidate, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            jo.put("from", socket.id())
            jo.put("to", to)
            socket.emit("message", jo)
//

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    fun sendSessionDescription(sdp: SessionDescription, to: String?, p: String?) {
        Log.e("in session desc", "in session desc")
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            jo.put("from", socket.id())
            jo.put("part", p)
            jo.put("to", to)
            socket.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    interface Callback {
        fun onCreateRoom(id: String)
        fun onPeerJoined(socketId: String)
        fun onSelfJoined()
        fun onPeerLeave(data: String)
        fun onOfferReceived(data: JSONObject)
        fun onAnswerReceived(data: JSONObject)
        fun onIceCandidateReceived(data: JSONObject)
    }

    companion object {
        private var instance: SignalingClient? = null
        fun get(): SignalingClient? {
            if (instance == null) {
                synchronized(SignalingClient::class.java) {
                    if (instance == null) {
                        instance = SignalingClient()
                    }
                }
            }
            return instance
        }
    }
}


