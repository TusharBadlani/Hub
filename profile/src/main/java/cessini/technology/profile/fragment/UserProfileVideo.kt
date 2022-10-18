package cessini.technology.profile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.cvo.exploremodels.ProfileModel
import cessini.technology.model.Video
import cessini.technology.newrepository.video.VideoRepository
import cessini.technology.profile.R
import cessini.technology.profile.controller.VideoController
import cessini.technology.profile.databinding.FragmentUserProfileVideoBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import cessini.technology.profile.viewmodel.PublicProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileVideo : Fragment() {
    companion object {
        private const val TAG = "UserProfileVideo"
    }

    val viewModel: ProfileViewModel by activityViewModels()
    private val galleryViewModel by activityViewModels<GalleryViewModel>()
    private val baseViewModel by activityViewModels<BaseViewModel>()

    private var _binding: FragmentUserProfileVideoBinding? = null
    val binding get() = _binding!!

    var controller: VideoController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_profile_video,
                container,
                false
            )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

//        setUpController(viewModel.profileVideoList)
        setUpController(viewModel.profileVideoListAPI)
        //to refresh the adapter every time a new video is added in the list

        if (!ProfileConstants.public) {
            viewModel.profileVideoListAPI.observe(viewLifecycleOwner) {
                Log.d("ProfileVideoFragment", "Size Of the Videos: ${it.size} ")
                if (!it.isNullOrEmpty()) {
                    activity?.run {
                        galleryViewModel.setCommonVideoProfile(
                            ProfileModel(
                                id = baseViewModel.id.value,
                                name = baseViewModel.name.value,
                                channel_name = baseViewModel.channelName.value,
                                profile_picture = baseViewModel.profileImage.value
                            )
                        )
                    }
                    Log.d(TAG, "Observed Length: ${it.size}")
                    binding.profileVideoText.visibility = View.GONE
                    binding.profileVideoLabel.visibility = View.GONE
                    binding.rvProfileVideo.visibility = View.VISIBLE
                    controller!!.allVideos = it
                    //viewModel.updateVideoAdapter()
                } else {
                    Log.d(TAG, "No Videos Found.")
                    binding.profileVideoLabel.visibility = View.VISIBLE
                    binding.profileVideoText.visibility = View.VISIBLE
                    binding.rvProfileVideo.visibility = View.GONE
                }
            }
        } else {
            viewModel.publicProfilerofileVideoListAPI.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    Log.d("UserProfVid", "Observed Length: ${it.size}")
                    binding.profileVideoText.visibility = View.GONE
                    binding.profileVideoLabel.visibility = View.GONE
                    binding.rvProfileVideo.visibility = View.VISIBLE
                    controller!!.allVideos = it
                    //viewModel.updateVideoAdapter()
                } else {
                    Log.d(TAG, "No Videos Found.")
                    binding.profileVideoLabel.visibility = View.VISIBLE
                    binding.profileVideoText.visibility = View.VISIBLE
                    binding.rvProfileVideo.visibility = View.GONE
                }
            }
        }


    }


    override fun onResume() {
        super.onResume()

        if (!ProfileConstants.public) {
            activity?.run {
                val base = (activity as HomeActivity).baseViewModel
                galleryViewModel.setCommonVideoProfile(
                    ProfileModel(
                        id = base.id.value,
                        name = base.name.value,
                        channel_name = base.channelName.value,
                        profile_picture = base.profileImage.value
                    )
                )
            }

        } else {
            activity?.run {
                val publicProfileVM = ViewModelProvider(this)[PublicProfileViewModel::class.java]
                galleryViewModel.setCommonVideoProfile(
                    ProfileModel(
                        id = ProfileConstants.creatorModel?.id,
                        name = publicProfileVM.displayName.value,
                        channel_name = publicProfileVM.channelName.value,
                        profile_picture = publicProfileVM.photoUrl.value
                    )
                )
            }
        }
    }

    @Inject
    lateinit var videoRepository: VideoRepository

    /**Setting up the Profile Viewer Epoxy Recycler View.*/
    private fun setUpController(profileVideoList: MutableLiveData<ArrayList<Video>>) {
        /**Setting up the Controller for the epoxy.*/
        controller =
            VideoController(activity, viewModel, this, videoRepository, ProfileConstants.public)

        binding.rvProfileVideo.setController(controller!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        controller!!.activity = null
        controller!!.viewModel = null
        controller!!.userProfileVideoContext = null

        controller = null
        _binding = null
    }
}
