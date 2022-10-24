package cessini.technology.profile.fragment.profileVideo

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.profile.R
import cessini.technology.profile.adapter.VideoAdapter
import cessini.technology.profile.databinding.FragmentProfileVideoBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileVideoFragment : BaseFragment<FragmentProfileVideoBinding>(R.layout.fragment_profile_video) {
    companion object {
        private const val TAG: String = "ProfileVideoFragment"
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**Setting Up the View Model as Profile View Model.*/
        viewModel = activity?.run {
            ViewModelProvider(this)[ProfileViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**Setting up the viewModel to the Binding.*/
        binding.viewModel = viewModel//attach your viewModel to xml
        /**Setting a lifecycleOwner as this Fragment.*/
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        if (ProfileConstants.videoModel == null) {
            viewModel.storiesList.observe(this, {
                if (it != null) {
                    Log.d(TAG, it.size.toString())
                    /** Setting up the ViewPager for the home fragment. */
                    binding.profileViewpager.apply {
                        adapter = VideoAdapter(it)
                    }
                    binding.profileViewpager.adapter?.notifyDataSetChanged()
                    binding.profileViewpager.setCurrentItem(viewModel.storyPosition.value!!, false)
                } else {
                    Log.d(TAG, "null")
                }
            })
        } else {
            val videoList = ArrayList<VideoModel>()
            val thumbnail : Bitmap? = viewModel.getBitmap(ProfileConstants.videoModel?.thumbnail!! , this)
            val video = VideoModel(
                thumbnail,
                ProfileConstants.videoModel?.title!!,
                ProfileConstants.videoModel?.description!!,
                ProfileConstants.videoModel?.upload_file!!,
                ProfileConstants.videoModel?.category!!.category!!

            )
            videoList.add(video)
            binding.profileViewpager.apply {
                adapter = VideoAdapter(videoList)
            }
            binding.profileViewpager.adapter?.notifyDataSetChanged()
            ProfileConstants.videoModel = null
        }

        /** Sending User Back to the Profile Fragment.*/
        binding.profileVideoBackNavigationButton.setOnClickListener {
            it.findNavController().navigateUp()
        }

    }
}