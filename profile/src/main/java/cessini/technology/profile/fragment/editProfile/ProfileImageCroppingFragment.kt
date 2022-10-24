package cessini.technology.profile.fragment.editProfile

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.presentation.HomeActivity
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.profile.R
import cessini.technology.profile.databinding.ProfileImageCroppingFragmentBinding
import cessini.technology.profile.utils.Constant
import cessini.technology.profile.viewmodel.EditUserProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileImageCroppingFragment :
    BaseBottomSheet<ProfileImageCroppingFragmentBinding>(R.layout.profile_image_cropping_fragment) {

    companion object {
        private const val TAG = "EditProfileCroppingFragment"
    }

    private val viewModel: EditUserProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.profileImageCroppingFragmentViewModel = viewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, settingBottomSheetHeight + 20)
        }
        return dialog
    }

    override fun onResume() {

        binding.profileImageCroppingView.params.circleRadius = 500

        /** Setting the image selected by the user in the cropper section. */
        if (Constant.imageURI != null) {
            binding.profileImageCroppingView.setImageURI(Uri.parse(Constant.imageURI))
        } else {
            binding.profileImageCroppingView.setImageResource(R.drawable.ic_user_defolt_avator)
        }

        /**Setting up the profile image. */
        binding.doneCroppingButton.setOnClickListener {
            viewModel.profileBitmap.value = binding.profileImageCroppingView.croppedBitmap
            viewModel.currentPictureUri.value =
                viewModel.saveImage(TAG,binding.profileImageCroppingView.croppedBitmap, "${viewModel.channelName.value}${viewModel.getTimeStamp()}.jpg" ,
                    activity as HomeActivity
                )
            findNavController().popBackStack(R.id.profileSettingFragment, false)
        }

        /** Sending user back to Edit Profile Fragment. */
        binding.backNavigationButtonProfileCroppingFragment.setOnClickListener {
            findNavController().navigateUp()
        }

        super.onResume()
    }

}
