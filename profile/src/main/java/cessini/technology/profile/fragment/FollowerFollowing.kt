package cessini.technology.profile.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant
import cessini.technology.profile.R
import cessini.technology.profile.adapter.FollowerFollowingAdapter
import cessini.technology.profile.databinding.FragmentFollowerFollowingBinding
import cessini.technology.profile.viewmodel.FollowerFollowingViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowerFollowing : BaseBottomSheet<FragmentFollowerFollowingBinding>(R.layout.fragment_follower_following) {

    private val viewModel: FollowerFollowingViewModel by activityViewModels()

    var count = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog,Constant.settingBottomSheetHeight)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if(count % 2 == 0){
                        dialog.dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

        })
        dialog.behavior.isDraggable = false
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.activity = requireActivity()
        arguments?.getString("profileId")?.let {
            viewModel.loadFollowingList(it)
        }
        binding.vpFollowerFollowing.currentItem = 0
    }

    override fun onResume() {
        super.onResume()

        binding.vpFollowerFollowing.adapter =
            FollowerFollowingAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(
            binding.tabFollowFollowing, binding.vpFollowerFollowing
        ) { tab: TabLayout.Tab, position: Int ->

            if (position == 0) {
                tab.text = "Followers"
            } else {
                tab.text = "Following"
            }

        }.attach()


        binding.backNavigationButtonFollowFollowingProfile.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
