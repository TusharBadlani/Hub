package cessini.technology.profile.fragment.publicProfile

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.common.BaseFragment
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.Navigator
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.profile.R
import cessini.technology.profile.adapter.ProfileTabViewAdapter
import cessini.technology.profile.databinding.FragmentPublicProfileBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import cessini.technology.profile.viewmodel.PublicProfileViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.jamshid.library.progress_bar.CircleProgressBar
import javax.inject.Inject


@AndroidEntryPoint
class PublicProfileFragment : BaseFragment<FragmentPublicProfileBinding>(R.layout.fragment_public_profile), MotionLayout.TransitionListener {
    companion object {
        private const val TAG = "PublicProfileFragment"
    }

    private val publicProfileViewModel: PublicProfileViewModel by activityViewModels()

    private lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var navigator: Navigator

    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel = activity?.run {
            ViewModelProvider(this)[ProfileViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        //let's register this app to the gcm if it hasn't been registered already.


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileViewModel = publicProfileViewModel//attach your viewModel to xml
        binding.lifecycleOwner = viewLifecycleOwner
        publicProfileViewModel.activity = requireActivity()

        profileViewModel.activity = requireActivity()

        //setting up the viewPager and the TabLayout
        binding.viewPager2PublicProfile.isSaveEnabled = false
        tabLayout = binding.profileTab

        binding.ufollow.setOnClickListener {
            findNavController().navigate(PublicProfileFragmentDirections.toFlo(publicProfileViewModel.currentId))
        }
        publicProfileViewModel.verified.observe(viewLifecycleOwner) {
            if(it) {
                binding.imageView8.visibility = VISIBLE
            }
            else {
                binding.imageView8.visibility = GONE
            }
        }

        //setting SwipeRefreshLayout
        binding.swipeRefreshLayout.apply {
            val cp = CircleProgressBar(requireContext())
            cp.setBorderWidth(2)
            binding.swipeRefreshLayout.setCustomBar(cp)
            setRefreshListener {
                Handler(Looper.getMainLooper()).postDelayed({

                    try {
                        showShimmer()
                        binding.swipeRefreshLayout.setRefreshing(false)
                    } catch (e: NullPointerException) {
                        //empty catch block
                    }
                }, 3000)
            }
        }

        binding.containerProfile.addTransitionListener(this)

    }

    private fun showShimmer() {

    }

    override fun onResume() {
        super.onResume()
        publicProfileViewModel.clearUserInfo()
        profileViewModel.clearPublicUserInfo()

        ProfileConstants.public = true
        if (ProfileConstants.story) {
            publicProfileViewModel.loadProfile(true)
            profileViewModel.loadProfile(true)
        } else {
            publicProfileViewModel.loadProfile(false)
            profileViewModel.loadProfile(false)
        }

        /**Hide the keyboard.*/
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

        publicProfileViewModel.followingStatus.observe(viewLifecycleOwner) {
            binding.btnFollow.visibility = View.VISIBLE
            if (it) {
                //binding.btnFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_folloyed))
                binding.btnFollow.setText("Unfollow")
                //binding.btnFollow.setTextColor(Color.rgb(118,118,118))
                binding.btnFollow.setTextColor(ContextCompat.getColor(requireActivity(),R.color.cpTextDark))
                binding.btnFollow.setBackgroundResource(R.drawable.unfollow_btn)
                //publicProfileViewModel.followingText.value = ""
            } else {
                //binding.btnFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_addfollower))
                binding.btnFollow.setText("Follow")
                binding.btnFollow.setTextColor(ContextCompat.getColor(requireActivity(),R.color.btnfllow))
                //binding.btnFollow.setTextColor(Color.rgb(239,239,239))
                binding.btnFollow.setBackgroundResource(R.drawable.join_myspace_upcoming)
                //publicProfileViewModel.followingText.value = ""
            }
        }

        if (true) {
            initiateListenersAndObsPublic()
        } else {
            initiateListenersAndObsPrivate()
        }

        /** Send User Back to The previous view.*/
        binding.backNavigationButtonPublicProfile.setOnClickListener {
            (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.ExploreFlow)
        }

        /**Sending User to the Public Room Section.*/
        binding.btnRoom.setOnClickListener { _ ->
//            var r1 = Room("1","Room1","Title1", Creator("ID903767960000000094","Tabish","",""),10,false,false, emptyList(), emptyList())
//            var r2 = Room("2","Room1","Title1", Creator("ID903767960000000094","Tabish","",""),10,false,false, emptyList(), emptyList())
//            profileViewModel.rooms.value = listOf(r1,r2)
//            Log.e("PublicProfile","${profileViewModel.rooms.value}")
//            profileViewModel.rooms.value?.let {
//                Log.e("PublicProfile","Inside ViewModelRoom")
//                if (it.isEmpty()) return@let
//
//                if (it.size == 1) {
//                    navigator.navigateToFlow(NavigationFlow.AccessRoomFlow(it.first().name))
//                    return@setOnClickListener
//                }
//
//                findNavController().navigate(
//                    PublicProfileFragmentDirections
//                        .actionPublicProfileFragmentToPublicRoomFragment(it.first().creator.id)
//                )
//                return@setOnClickListener
//            }

//            Log.e("PublicProfile","Outside ViewModelRoom")
//            Log.e("currId","${publicProfileViewModel.currentId}")
//            Log.e("channel","${publicProfileViewModel.channelName.value.toString()}")
            findNavController().navigate(
                PublicProfileFragmentDirections
                    .actionPublicProfileFragmentToPublicProfileRoomMessage(publicProfileViewModel.currentId,publicProfileViewModel.channelName.value.toString())
            )
            return@setOnClickListener

            ProfileConstants.story = false
//            findNavController().navigate(R.id.action_publicProfileFragment_to_roomFragment3)
        }
        binding.addFriend.setOnClickListener {
            (activity as HomeActivity).baseViewModel.loadUserInfo()
            val bundle = Bundle()

            (activity as HomeActivity).baseViewModel.name.observe(viewLifecycleOwner){
                if (!(it == null)){
                    bundle.putString("username", it.toString())
                }
            }
            (activity as HomeActivity).baseViewModel.profileImage.observe(viewLifecycleOwner){
                if (!(it == null)){
                    bundle.putString("profileimg", it.toString())
                }
            }
            bundle.putString("sharelink","Let's talk with best creators who help you on Myworld! " +
                    "It's a fast, simple and secure app. " +
                    "Get it at https://play.google.com/store/apps/details?id=cessini.technology.myworld")
            findNavController().navigate(R.id.action_global_share,bundle)
        }
        publicProfileViewModel.profileLoadProgress.observe(viewLifecycleOwner, {
            when (it) {
                0 -> {
                    binding.viewPager2PublicProfile.visibility = View.GONE
                    Log.d(TAG, it.toString())
                }
                100 -> {
                    binding.viewPager2PublicProfile.visibility = View.VISIBLE
                    Log.d(TAG, it.toString())
                    profileDetails()
                }
                else -> {
                    binding.viewPager2PublicProfile.visibility = View.VISIBLE
//                    Toast.makeText(activity, "Profile Load Failed", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.toString())
                }
            }
        })
        if(binding.containerProfile.currentState == R.id.end) {
            binding.swipeRefreshLayout.isEnabled = false
        }
    }

    private fun profileDetails() {
        //Setting up the ViewPager Adapter and The TabLayout
        binding.viewPager2PublicProfile.isSaveEnabled = false
        binding.viewPager2PublicProfile.adapter =
            ProfileTabViewAdapter(this.childFragmentManager, lifecycle)
        TabLayoutMediator(
            tabLayout,
            binding.viewPager2PublicProfile
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Hub"
                }
                1 -> {
                    tab.text = "Video"
                }
                2 -> {
                    tab.text = "Save"
                }

            }
        }.attach()
    }

    private fun initiateListenersAndObsPrivate() = Unit


    private fun initiateListenersAndObsPublic() {
        binding.backNavigationButtonPublicProfile.setOnClickListener {
            Log.d("PublicProfileFragment", "Back Navigation Pressed")
            (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.ExploreFlow)
        }
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        binding.swipeRefreshLayout.isEnabled = false
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) = Unit

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        if (p0 != null) {
            if(p0.currentState == R.id.start) {
                binding.swipeRefreshLayout.isEnabled = true
            }

        }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit
}
