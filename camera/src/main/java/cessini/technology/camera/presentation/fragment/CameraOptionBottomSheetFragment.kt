package cessini.technology.camera.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cessini.technology.camera.R
import cessini.technology.camera.databinding.FragmentCameraOptionBottomSheetBinding
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.myspace.create.CreateRoomFragment
import cessini.technology.myspace.create.CreateRoomSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class CameraOptionBottomSheetFragment
    :
    BaseBottomSheet<FragmentCameraOptionBottomSheetBinding>(R.layout.fragment_camera_option_bottom_sheet),
    BottomSheetLevelInterface {
    var count = 0


    private val viewModel: GalleryViewModel by activityViewModels()
    private val createRoomViewModel : CreateRoomSharedViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, Constant.settingBottomSheetHeight)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if (count % 2 != 0) {
                        dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintLayout3.progress = 0f

        createRoomViewModel.bottomSheetDraggedState.observe(viewLifecycleOwner) { state->
            val shiftedState = (1+state)/2f;
            setMotionLevel(-shiftedState)

        }

        binding.selectStory.setOnClickListener {
            viewModel.setUploadType(0)
            viewModel.isOpenFirstTime = false
            findNavController().navigate(CameraOptionBottomSheetFragmentDirections.actionCameraOptionBottomSheetFragmentToCameraFragment())
            dismiss()
        }

        binding.imageView3.setOnClickListener {
            viewModel.setUploadType(0)
            viewModel.isOpenFirstTime = false
            findNavController().navigate(CameraOptionBottomSheetFragmentDirections.actionCameraOptionBottomSheetFragmentToCameraFragment())
            dismiss()
        }

        binding.selectCamera.setOnClickListener {
            viewModel.setUploadType(1)
            viewModel.isOpenFirstTime = false
            findNavController().navigate(CameraOptionBottomSheetFragmentDirections.actionCameraOptionBottomSheetFragmentToCameraFragment())
            dismiss()
        }
        binding.imageView7.setOnClickListener {
            viewModel.setUploadType(1)
            viewModel.isOpenFirstTime = false
            findNavController().navigate(CameraOptionBottomSheetFragmentDirections.actionCameraOptionBottomSheetFragmentToCameraFragment())
            dismiss()
        }

        binding.textView11.setOnClickListener {
            val bottomSheet = CreateRoomFragment(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
        binding.imageView8.setOnClickListener {
            val bottomSheet = CreateRoomFragment(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.roomNoticeDropdownIcon.setOnClickListener {
            if (viewModel.isOpenFirstTime) {
                viewModel.isOpenFirstTime = true
                findNavController().popBackStack()
            } else {
                dismiss()
            }
        }
    }

    override fun onSheet2Dismissed() {
        setLevel(-1)
    }

    override fun onSheet2Created() {
        setLevel(-2)
    }

    override fun onSheet1Dismissed() {
        setLevel(0)
    }

    override fun getHeightOfBottomSheet(height: Int) {
        binding.cameraOptionConstraintLayout.layoutParams.height = height + 10.toPx().toInt()
        setLevel(-1)
    }
}