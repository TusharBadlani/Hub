package cessini.technology.profile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.model.Room
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.profile.R
import cessini.technology.profile.controller.UserHubController
import cessini.technology.profile.databinding.FragmentUserProfileStoryBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileRoom() : Fragment() {
    companion object {
        private const val TAG = "ProfileStoryFragment"
    }

    private var _binding: FragmentUserProfileStoryBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var roomRepository: RoomRepository
    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    //var controller: StoryController? = null
    var controller: UserHubController? = null

    var flag:Boolean=false

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**Setting Up the View Model as Profile View Model.*/
        viewModel = activity?.run {
            ViewModelProvider(this)[ProfileViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_profile_story,
            container,
            false
        )

        setUpController()


        binding.userProfileStoryViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated: At least It's getting created")

        viewModel.rooms.observe(viewLifecycleOwner) {
            Log.e(TAG, "${viewModel.rooms.value}")
            if (!it.isNullOrEmpty()) {
                Log.e(TAG, "${it}")
                controller!!.allRoom = mutableListOf()
                controller!!.allRoom = it.toMutableList()
                controller!!.roomFilterList = mutableListOf<Room>() as ArrayList<Room>
                controller!!.roomFilterList = it as ArrayList<Room>
                hideShimmer()

            } else {
                Log.e(TAG, "No Stories Found. None Are available")
                noData()

            }
            //hideShimmer()
        }
//        if (!ProfileConstants.public) {
//
//            viewModel.profileStoriesListAPI.observe(viewLifecycleOwner, {
//
//                if (!it.isNullOrEmpty()) {
//                    hideShimmer()
//                    Log.d(TAG, "List Size : ${it.size}")
//
//                    controller!!.allViewers = it
//
//                    activity?.run {
//                        val galleryVM = ViewModelProvider(this)[GalleryViewModel::class.java]
//                        galleryVM.commonStoryProfile.value =
//                            StoryProfileModel(
//                                id = (activity as HomeActivity).baseViewModel.id.value,
//                                profilePicture = (activity as HomeActivity).baseViewModel.profileImage.value,
//                                name = (activity as HomeActivity).baseViewModel.name.value
//                            )
//                    }
//
//
//                } else {
//                    Log.d(TAG, "No Stories Found. None Are available")
//                    noData()
//                    binding.storyProfileFragmentRecyclerView.visibility = View.GONE
//                }
//            })
//        } else if(ProfileConstants.public){
//            viewModel.publicProfileStoriesListAPI.observe(viewLifecycleOwner, {
//
//                if (!it.isNullOrEmpty()) {
//                    hideShimmer()
//                    binding.profileStoryText.visibility = View.GONE
//                    binding.noStoryLabel.visibility = View.GONE
//                    controller!!.allViewers = it
//
//                    activity?.run {
//                        val publicProfileVM =
//                            ViewModelProvider(this)[PublicProfileViewModel::class.java]
//                        val galleryVM = ViewModelProvider(this)[GalleryViewModel::class.java]
//                        galleryVM.commonStoryProfile.value =
//                            StoryProfileModel(
//                                id = ProfileConstants.creatorModel?.id.toString(),
//                                profilePicture = publicProfileVM.photoUrl.value,
//                                name = publicProfileVM.displayName.value
//                            )
//                    }
//
//
//                } else {
//                    Log.d(TAG, "No Stories Found.")
//                    noData()
//                    binding.storyProfileFragmentRecyclerView.visibility = View.GONE
//
//                }
//            })
//        }else{
//            hideShimmer()
//        }
    }


    /**Setting up the Profile Viewer Epoxy Recycler View.*/
    private fun setUpController() {
        /**Setting up the Controller for the epoxy.*/
//        controller = StoryController(viewModel, this)
//
//        binding.storyProfileFragmentRecyclerView.setController(controller!!)
        Log.d("SetUpController","Called")
        binding.storyProfileFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            controller = UserHubController(context,requireActivity(),this@UserProfileRoom,viewModel,roomRepository,userIdentifierPreferences)
            setController(controller!!)
        }
    }

    private fun hideShimmer() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            binding.profileRoomShimmer.visibility = View.GONE
            binding.storyProfileFragmentRecyclerView.visibility = View.VISIBLE
            binding.profileStoryText.visibility = View.GONE
            binding.noStoryLabel.visibility = View.GONE

        }
    }

    private fun noData() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            binding.profileRoomShimmer.visibility = View.GONE
            binding.storyProfileFragmentRecyclerView.visibility=View.GONE
            binding.noStoryLabel.visibility = View.VISIBLE
            binding.profileStoryText.visibility = View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        Log.e(TAG,"On Destroy View Called")
        //controller!!.context = null
        //controller!!.viewmodel = null

        //controller!!.allRoom= mutableListOf()
        //controller!!.roomFilterList= mutableListOf<Room>() as ArrayList<Room>

//        controller!!.context=null
        controller = null
        _binding = null
    }
}
