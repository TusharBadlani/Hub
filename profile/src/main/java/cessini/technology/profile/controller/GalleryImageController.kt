package cessini.technology.profile.controller

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.AsyncEpoxyController
import cessini.technology.profile.fragment.editProfile.ImageGalleryBottomSheetFragment
import cessini.technology.profile.utils.Constant
import cessini.technology.profile.R
import cessini.technology.profile.galleryRecyclerRow

class GalleryImageController(
    var activity: FragmentActivity?,
    var imageGalleryBottomSheetFragment: ImageGalleryBottomSheetFragment?,
) : AsyncEpoxyController() {
    override fun buildModels() {
        allImages.forEachIndexed { index, image ->
            galleryRecyclerRow {
                id(index)
                /**Setting up the Image in the Image View using the DataBinding.*/
                galleryImage(image)
                /**When the Image is Clicked then We send user to the profileCropping Section.*/
                onImageClicked { _ ->
                    Constant.imageURI?.let { Log.i("URi", it) }

                    /** Storing the path of the clicked picture in the imageURI from the Constant class. */

                    /** Storing the path of the clicked picture in the imageURI from the Constant class. */
                    Constant.imageURI = image

                    imageGalleryBottomSheetFragment!!.findNavController()
                        .navigate(R.id.global_action_to_profileImageCroppingFragment)

                }
            }
        }
    }

    /**Setting up the Image List.*/
    var allImages: ArrayList<String> =
        arrayListOf()
        set(value) {
            field = value
            requestModelBuild()
        }
}