package cessini.technology.commonui.data.models

import cessini.technology.commonui.presentation.uiutils.FileRenderer
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
