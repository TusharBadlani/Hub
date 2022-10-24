package cessini.technology.commonui.data.webrtc.live

import android.util.Log
import org.webrtc.*
import org.webrtc.PeerConnection.*

open class PeerConnectionAdapter(tag: String) : Observer {
    private val tag: String = "chao $tag"
    private fun log(s: String) {
        Log.d(tag, s)
    }

    override fun onSignalingChange(signalingState: SignalingState) {
        log("onSignalingChange $signalingState")
    }

    override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
        log("onIceConnectionChange $iceConnectionState")
    }

    override fun onIceConnectionReceivingChange(b: Boolean) {
        log("onIceConnectionReceivingChange $b")
    }

    override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
        log("onIceGatheringChange $iceGatheringState")
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        log("onIceCandidate $iceCandidate")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
        log("onIceCandidatesRemoved $iceCandidates")
    }

    override fun onAddStream(mediaStream: MediaStream) {
        log("onAddStream $mediaStream")
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        log("onRemoveStream $mediaStream")
    }

    override fun onDataChannel(dataChannel: DataChannel) {
        log("onDataChannel $dataChannel")
    }

    override fun onRenegotiationNeeded() {
        log("onRenegotiationNeeded ")
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
        log("onAddTrack $mediaStreams")
    }

}
