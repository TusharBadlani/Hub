package cessini.technology.profile.fragment.settings

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.profile.R
import cessini.technology.profile.databinding.UserProfileSettingsBinding
import cessini.technology.profile.fragment.editProfile.EditUserProfileFragment
import cessini.technology.profile.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ProfileSettingFragment
    : BaseBottomSheet<UserProfileSettingsBinding>(R.layout.user_profile_settings),
    BottomSheetLevelInterface {

    private val baseViewModel: BaseViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
        }

        binding.userProfileSettingViewModel = profileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //to extend fragment to top
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.applink.setOnClickListener {
            val uri = Uri.parse("https://joinmyworld.in/faq.html")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            baseViewModel.isDarkThemeEnabled.collectLatest { isDarkThemeEnabled ->
                // Keep theme switch synced with latest theme state
                binding.changeThemeSwitch.isChecked = isDarkThemeEnabled
            }
        }

        binding.changeThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            baseViewModel.setDarkThemeEnabled(isChecked)
        }

        binding.userSittingLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                userIdentifierPreferences.uuid = UUID.randomUUID().toString()

                (activity as HomeActivity).signOut()
                profileViewModel.clearProfileDataAfterLogOut()
                findNavController().popBackStack()
            }
        }

        binding.userEditSitting.setOnClickListener {
            val bottomSheet = EditUserProfileFragment(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog,settingBottomSheetHeight)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_SETTLING) {
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





    override fun onResume() {
        binding.buttonCheck = true

        binding.backNavigationButtonSettingProfile.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.userSettingDiscovery.setOnClickListener {
            val bottomSheet = DiscoveryFragment(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.userRevenueSitting.setOnClickListener {
            val bottomSheet = EarningFragment(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.userPaymentOptions.setOnClickListener {
            val bottomSheet = PaymentsFragment(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }


        super.onResume()
    }

    override fun onSheet2Dismissed() {
        binding.userProfileScrollView.layoutParams.height = -1
        setLevel(-1)
    }

    override fun onSheet2Created() {
        setLevel(-2)
    }

    override fun onSheet1Dismissed() {

        setLevel(0)
    }

    override fun getHeightOfBottomSheet(height: Int) {
        binding.userProfileScrollView.layoutParams.height = height + 10.toPx().toInt()
        setLevel(-1)
    }

}
