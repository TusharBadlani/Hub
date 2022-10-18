package cessini.technology.newrepository.cameraRepository


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import javax.inject.Inject


class VideoGalleryRepository @Inject constructor() {
    companion object {
        private const val TAG = "VideoGalleryRepository"
    }

    var listOfAllVideos = ArrayList<String>()

    /** Function to call the Viewer Upload API.*/

    @SuppressLint("NewApi")
    fun loadVideoPathList(context: Context): ArrayList<String> {
        /**For Video Fetching */
        loadExternalVideos(context)
        loadInternalVideos(context)
        return listOfAllVideos
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadExternalVideos(context: Context) {

        //External Storage File
        val externalVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI


        val projectionVideo =
            arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            )


        val orderByVideo = MediaStore.Video.Media.DATE_TAKEN

        val externalCursorVideo: Cursor? = context.contentResolver.query(
            externalVideoUri,
            projectionVideo,
            null,
            null,
            "$orderByVideo DESC"
        )
        val externalColumnIndexDataVideo =
            externalCursorVideo?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)


        var absolutePathOfImage: String? = null
        if (externalCursorVideo != null) {
            while (externalCursorVideo.moveToNext()) {
                absolutePathOfImage =
                    externalColumnIndexDataVideo?.let { externalCursorVideo.getString(it) }

                if (absolutePathOfImage != null) {
                    listOfAllVideos.add(absolutePathOfImage)
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadInternalVideos(context: Context) {


        val internalVideoUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI
        val projectionVideo =
            arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            )

        val orderByVideo = MediaStore.Video.Media.DATE_TAKEN

        val internalCursorVideo: Cursor? = context.contentResolver.query(
            internalVideoUri,
            projectionVideo,
            null,
            null,
            "$orderByVideo DESC"
        )


        //Internal Storage File
        val internalColumnIndexDataVideo =
            internalCursorVideo?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)


        var absolutePathOfImage: String? = null
        if (internalCursorVideo != null) {
            while (internalCursorVideo.moveToNext()) {
                absolutePathOfImage =
                    internalColumnIndexDataVideo?.let {
                        internalCursorVideo.getString(
                            it
                        )
                    }

                if (absolutePathOfImage != null) {
                    listOfAllVideos.add(absolutePathOfImage)
                }
            }
        }
    }

}