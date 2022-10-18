package cessini.technology.commonui.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.webkit.MimeTypeMap
import android.widget.Toast

class Constant {
    companion object {
        const val PERMISSION_CODE: Int = 1

        var currentVolumeOfExoPlayer: Float = 0f

        var isMute: Boolean = false

        var homeFeedCurrentPositionAttached: Int = 0

        var homeFeedCurrentPositionDetached: Int = 0

        var bottomSheetFullScreenTopMargin: Double = 0.15

        var settingBottomSheetHeight: Int = 80

        var manageMySpaceHeight: Int = 0

        fun showToast(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        }
    }

    /**
     * Get the Type of The data or file extension
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}