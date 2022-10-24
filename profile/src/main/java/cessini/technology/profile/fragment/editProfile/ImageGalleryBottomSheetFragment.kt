package cessini.technology.profile.fragment.editProfile

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.presentation.globalviewmodels.GalleryViewModel
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.profile.R
import cessini.technology.profile.`class`.Gallery
import cessini.technology.profile.controller.GalleryImageController
import cessini.technology.profile.databinding.BottomSheetGalleryLayoutBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageGalleryBottomSheetFragment(private val listener: BottomSheetLevelInterface) :
    BaseBottomSheet<BottomSheetGalleryLayoutBinding>(R.layout.bottom_sheet_gallery_layout) {

    val viewModel: GalleryViewModel by activityViewModels()

    private val profileViewModel: ProfileViewModel by viewModels()

    var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.onSheet2Created()
        binding.galleryBottomSheetViewModel = profileViewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog,settingBottomSheetHeight + 12)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if (count % 2 == 0) {
                        dismiss()
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
        return dialog
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        loadImageFromGallery()
        super.onStart()
    }

    var path: String? = null

    /** Load All Images From the Storage and display them in the bottom sheet model. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadImageFromGallery() {
        val model = ViewModelProvider(this).get(Gallery::class.java)
        if (path != null) {
            model.listOfAllImage.add(0, path!!)
        }
        //var image = context?.let { model.listOfVideos(it) }!!
        val image: ArrayList<String>? = context?.let { model.listOfImages(it) }

        Log.i("List", image.toString())

        /**Setting up the Controller for the epoxy.*/
        controller = GalleryImageController(requireActivity(), this)

        /** Setting up the Gallery Recycler View For Image Fetching */
        binding.recyclerView.setController(controller!!)
        if (image != null) {
            /**Sending the VideoList to the Controller by assigning the variable.*/
            controller!!.allImages = image
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onSheet1Dismissed()
        controller!!.activity = null
        controller!!.imageGalleryBottomSheetFragment = null

        controller = null
    }

    var controller: GalleryImageController? = null

}
