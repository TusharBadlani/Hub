package cessini.technology.cvo.cameraModels

import android.graphics.Bitmap
import java.io.Serializable

data class VideoModel(
        var videoThumbnail: Bitmap?,
        var videoTitle: String,
        var videoDesc: String,
        var videoUrl: String,
        var videoCategory : String
):Serializable
