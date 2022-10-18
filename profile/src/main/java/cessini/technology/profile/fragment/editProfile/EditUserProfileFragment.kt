package cessini.technology.profile.fragment.editProfile

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.common.toast
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.newapi.SharedLocationManager
import cessini.technology.profile.R
import cessini.technology.profile.databinding.FragmentEditUserProfileBinding
import cessini.technology.profile.utils.Constant
import cessini.technology.profile.viewmodel.EditUserProfileViewModel
import cessini.technology.profile.viewmodel.EditUserProfileViewModel.Event.ProfileUpdated
import cessini.technology.profile.viewmodel.EditUserProfileViewModel.Event.ShowToast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditUserProfileFragment(
    private val listener: BottomSheetLevelInterface
) : BaseBottomSheet<FragmentEditUserProfileBinding>(R.layout.fragment_edit_user_profile),
    BottomSheetLevelInterface {

    companion object {
        private const val TAG = "EditProfileFragment"
    }

    private val TAG: String = "Edit User Fragment"
    private val editUserProfileViewModel: EditUserProfileViewModel by activityViewModels()

    private lateinit var baseViewModel: BaseViewModel

    private var bottomSheet: ImageGalleryBottomSheetFragment? = null

    @Inject
    lateinit var sharedLocationManager: SharedLocationManager

    var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseViewModel = (activity as HomeActivity).baseViewModel
        returnHeightWhenReady()
        collectProfile()
        listenEvent()
        setupSaveProfileButton()
        setupCheckChannelNameButton()
        observeEditedProfilePictureUri()
        setupLocationUpdateButton()

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, settingBottomSheetHeight + 6)
        }

        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
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

    private fun returnHeightWhenReady() {
        binding.editUserProfileConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.editUserProfileConstraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(
                    this
                )
                listener.getHeightOfBottomSheet(binding.editUserProfileConstraintLayout.height)
            }

        })
    }

    private fun setupLocationUpdateButton() {
        baseViewModel.currentLocation.observe(viewLifecycleOwner) {
            if (it.isNullOrBlank()) return@observe

            binding.userLocation.text = it
        }

        binding.userLocation.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), cessini.technology.commonui.utils.Constant.PERMISSION_CODE
                )
                return@setOnClickListener
            }

            if (!isLocationEnabled()) {
                toast("Your Location is turned off.\n   Please turn it on.")
                requireActivity().startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                sharedLocationManager.locationFlow()
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .map { sharedLocationManager.getAddress(it.latitude, it.longitude) }
                    .catch { Log.e(TAG, "Something went wrong.") }
                    .collect {
                        Log.d(TAG, "Address of the user $it")
                        baseViewModel.setCurrentLocation(it)
                    }
            }

        }
    }

    /** Function to return
     * true -> Location (GPS) is Enabled.
     * false -> Location(GPS) is Disabled.*/
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }


    private fun observeEditedProfilePictureUri() {
        editUserProfileViewModel.currentPictureUri.observe(viewLifecycleOwner) {
            binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_enable_viewbutton)
            binding.saveButtonEditProfile.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            if (it != null) {
                Glide.with(binding.editprofileCardView)
                    .load(File(it))
                    .into(binding.editprofileCardView)
            }
            bottomSheet?.dismiss()
        }
    }

    private fun setupCheckChannelNameButton() {
        binding.uepEditChanelname.doOnTextChanged { text, _, _, _ ->
            editUserProfileViewModel.profileChannelName.value = text.toString()
        }

        editUserProfileViewModel.profileChannelName.observe(viewLifecycleOwner) {
            binding.availChannelName.visibility =
                if (it.isBlank() || it == binding.profile!!.channelName) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }

        binding.availChannelName.setOnClickListener {
            editUserProfileViewModel.checkChannelNameIsAvailable(
                binding.uepEditChanelname.text.toString()
            )
        }
    }

    private fun setupSaveProfileButton() {
        binding.saveButtonEditProfile.setOnClickListener {
            validate(message = "Enter a name", { return@setOnClickListener }) {
                binding.uepUsername.text.isNotBlank()
            }

            validate(message = "Enter a channel name", { return@setOnClickListener }) {
                binding.uepEditChanelname.text.isNotBlank()
            }

            val newChannelName = binding.uepEditChanelname.text.toString()
            val newUsername = binding.uepUsername.text.toString()
            val newBio = binding.uepEdit.text.toString()
            val newLocation = baseViewModel.currentLocation.value.orEmpty()
            val newEmail = binding.email.text.toString()
            val newExpertise = binding.expertise.text.toString()

            it.isEnabled = false
            binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_viewbutton)
            binding.saveButtonEditProfile.text = ""
            binding.btnProgress.isVisible = true
            editUserProfileViewModel.updateProfile(
                name = if (newUsername == binding.profile!!.name) "" else newUsername,
                bio = if (newBio == binding.profile!!.bio) "" else newBio,
                channelName = if (newChannelName == binding.profile!!.channelName) "" else newChannelName,
                location = if (newLocation == binding.profile!!.location) "" else newLocation,
                email = if (newEmail == binding.profile!!.email) "" else newEmail,
                expertise = if (newExpertise == binding.profile!!.expertise) "" else newExpertise,
            )
        }
    }

    private fun collectProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            editUserProfileViewModel.profile.collectLatest { binding.profile = it }
        }
    }

    private fun listenEvent() {
        editUserProfileViewModel.events.onEach {
            when (it) {
                is ShowToast -> toast(it.message)
                ProfileUpdated -> {
                    delay(500)
                    binding.btnProgress.isVisible = false
                    toast(message = "Profile updated")
                    dismiss()
                    findNavController().popBackStack(R.id.profileFragment, false)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private inline fun validate(message: String, ifFalse: () -> Unit, block: () -> Boolean) {
        if (block()) return

        toast(message)
        ifFalse()
    }

    /** Setup User Profile Image in Edit Profile Section.
     * Can Choose Image from Gallery or can click from camera and update the profile image.
     */
    private fun setUpProfileImage() {
        /** Opens a bottom sheet Dialog for the user.
         * Giving two Choices .
         * 1st - They can choose to have a profile image by clicking the picture from camera.
         * 2nd - They can choose to have a profile image by choosing the picture from the gallery.
         */
        bottomSheet = ImageGalleryBottomSheetFragment(this)
        bottomSheet!!.show(parentFragmentManager, bottomSheet!!.tag)
    }

    override fun onResume() {
        super.onResume()


        /** Setting and Updating the User Profile Picture. */
        binding.editProfileImage.setOnClickListener {

            setUpFragment()
        }

        binding.uepUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_enable_viewbutton)
                binding.saveButtonEditProfile.setTextColor(resources.getColor(R.color.white))
                if (binding.uepUsername.text?.toString().isNullOrBlank()) {
                    binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_viewbutton)
                    binding.saveButtonEditProfile.setTextColor(resources.getColor(R.color.cpHelpText))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.uepEditChanelname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_enable_viewbutton)
                binding.saveButtonEditProfile.setTextColor(resources.getColor(R.color.white))
                if (binding.uepEditChanelname.text?.toString().isNullOrBlank()) {
                    binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_viewbutton)
                    binding.saveButtonEditProfile.setTextColor(resources.getColor(R.color.cpHelpText))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.uepEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_enable_viewbutton)
                binding.saveButtonEditProfile.setTextColor(resources.getColor(R.color.white))
                if (binding.uepEdit.text?.toString().isNullOrBlank()) {
                    binding.saveButtonEditProfile.setBackgroundResource(R.drawable.round_viewbutton)
                    binding.saveButtonEditProfile.setTextColor(resources.getColor(R.color.cpHelpText))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.backNavigationButtonEditProfile.setOnClickListener {
            if (editUserProfileViewModel.profileBitmap.value != null) {
                editUserProfileViewModel.deleteOldFile(editUserProfileViewModel.currentPictureUri.value)
                editUserProfileViewModel.currentPictureUri.value = null
            }
            editUserProfileViewModel.profileBitmap.value = null
            dismiss()
        }
    }

    /** Setup the fragment when the Image Chooser Fragment is in the window.*/
    private fun setUpFragment() {
        /** Permissions checks */
        if ((activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.RECORD_AUDIO
                )
            } != PackageManager.PERMISSION_GRANTED)) {
            askPermission()
        } else {
            /** Setting and Updating the User Profile Picture. */
            setUpProfileImage()
        }
    }

    /** Ask For Permissions */
    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            Constant.PERMISSION_REQUEST_CODE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_profile_toolbar_menu, menu)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onSheet1Dismissed()
    }

    override fun onStop() {
        super.onStop()
        /** Resetting the profile picture to original state.*/
        editUserProfileViewModel.profileBitmap.value = null
    }

    override fun onDestroy() {
        super.onDestroy()
        /** Resetting the profile picture to original state.*/
        editUserProfileViewModel.profileBitmap.value = null
    }

    override fun onSheet2Dismissed() = Unit

    override fun onSheet2Created() {
        setLevel(-1)
        listener.onSheet2Created()
    }

    override fun onSheet1Dismissed() {
        setLevel(0)
        listener.onSheet2Dismissed()
    }

    override fun getHeightOfBottomSheet(height: Int) = Unit
}