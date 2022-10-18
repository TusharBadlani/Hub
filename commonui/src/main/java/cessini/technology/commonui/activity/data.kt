package cessini.technology.commonui.activity

import org.webrtc.EglBase
import org.webrtc.VideoTrack

data class data(
    var title: String,
    var index: Int,
    var video: VideoTrack,
    val creater: Boolean,
    val con: EglBase.Context,
    val fileRenderer: FileRenderer?
)
