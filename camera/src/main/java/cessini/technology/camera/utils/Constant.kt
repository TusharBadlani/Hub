package cessini.technology.camera.utils

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.camera.core.Camera
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture

class Constant {
    companion object {
        //Permission request code
        const val PERMISSION_REQUEST_CODE = 0

        //Permission is granted or not
        var isPermission: Boolean = false

        var SONG_NAME: String? = null

        //Camera Fragment
        var camera: Camera? = null
        var preview: Preview? = null
        var recordVideo: VideoCapture? = null

        var count: Int = 2

        var isRecording: Boolean = false

        var isFlash: Boolean = false

        var isPlaying = ""

        var selectSong = ""

        var mediaPlayer: MediaPlayer = MediaPlayer()

        var oldParentPos = -1
    }

    /**
     * Get the Type of The data or file extension *
     * */

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}