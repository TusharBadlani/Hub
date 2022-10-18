package cessini.technology.profile.`class`

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel

open class Gallery : ViewModel() {

    var listOfAllImage = ArrayList<String>()
    private var absolutePathOfImage: String? = null

    /** Get all the video from the storage and return their URI as a String . */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun listOfImages(context: Context): ArrayList<String> {
        /** For Image */
        val externalImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val internalImageUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val oderBy = MediaStore.Images.Media.DATE_TAKEN
        val externalCursor: Cursor? = context.contentResolver.query(
            externalImageUri, projection, null, null,
            "$oderBy DESC"
        )
        val internalCursor: Cursor? = context.contentResolver.query(
            internalImageUri, projection, null, null,
            "$oderBy DESC"
        )

        //External Storage File
        val externalColumnIndexData =
            externalCursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        if (externalCursor != null) {
            while (externalCursor.moveToNext()) {
                absolutePathOfImage = externalColumnIndexData?.let { externalCursor.getString(it) }

                if (absolutePathOfImage != null) {
                    listOfAllImage.add(absolutePathOfImage!!)
                }
            }
        }
        //Internal Storage File
        val internalColumnIndexData =
            internalCursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        if (internalCursor != null) {
            while (internalCursor.moveToNext()) {
                absolutePathOfImage = internalColumnIndexData?.let { internalCursor.getString(it) }

                if (absolutePathOfImage != null) {
                    listOfAllImage.add(absolutePathOfImage!!)
                }
            }
        }
        return listOfAllImage
    }
}