package cessini.technology.commonui.services.screen_share

import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtsp.utils.ConnectCheckerRtsp

interface ConnectCheckerRtp: ConnectCheckerRtmp, ConnectCheckerRtsp {

    fun onConnectionStartedRtp(rtpUrl: String)

    fun onConnectionSuccessRtp()

    fun onConnectionFailedRtp(reason: String)

    fun onNewBitrateRtp(bitrate: Long)

    fun onDisconnectRtp()

    fun onAuthErrorRtp()

    fun onAuthSuccessRtp()

    override fun onConnectionStartedRtmp(rtmpUrl: String) {
        onConnectionStartedRtp(rtmpUrl)
    }

    override fun onConnectionSuccessRtmp() {
        onConnectionSuccessRtp()
    }

    override fun onConnectionFailedRtmp(reason: String) {
        onConnectionFailedRtp(reason)
    }

    override fun onNewBitrateRtmp(bitrate: Long) {
        onNewBitrateRtp(bitrate)
    }

    override fun onDisconnectRtmp() {
        onDisconnectRtp()
    }

    override fun onAuthErrorRtmp() {
        onAuthErrorRtp()
    }

    override fun onAuthSuccessRtmp() {
        onAuthSuccessRtp()
    }

    override fun onConnectionStartedRtsp(rtspUrl: String) {
        onConnectionStartedRtp(rtspUrl)
    }

    override fun onConnectionSuccessRtsp() {
        onConnectionSuccessRtp()
    }

    override fun onConnectionFailedRtsp(reason: String) {
        onConnectionFailedRtp(reason)
    }

    override fun onNewBitrateRtsp(bitrate: Long) {
        onNewBitrateRtp(bitrate)
    }

    override fun onDisconnectRtsp() {
        onDisconnectRtp()
    }

    override fun onAuthErrorRtsp() {
        onAuthErrorRtp()
    }

    override fun onAuthSuccessRtsp() {
        onAuthSuccessRtp()
    }

}