package cessini.technology.commonui.fragment.storydisplay

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cessini.technology.commonui.R
import cessini.technology.commonui.`class`.ExoProvider
import cessini.technology.commonui.adapter.storydisplay.CommonStoryDisplayAdapter
import cessini.technology.commonui.common.BaseFragment
import cessini.technology.commonui.databinding.FragmentCommonStoryDisplayBinding
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.cvo.homemodels.StoriesFetchModel
import cessini.technology.cvo.profileModels.StoryProfileModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import dagger.hilt.android.AndroidEntryPoint
import kohii.v1.exoplayer.Kohii

@AndroidEntryPoint
class CommonStoryDisplayFragment :
    BaseFragment<FragmentCommonStoryDisplayBinding>(R.layout.fragment_common_story_display),
    CommonStoryDisplayAdapter.Listener {

    companion object {
        private val TAG = CommonStoryDisplayFragment::class.java.simpleName
    }

    private val galleryViewModel: GalleryViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()

    private val adapter: CommonStoryDisplayAdapter by lazy {
        CommonStoryDisplayAdapter(
            this,
            exoProvider
        )
    }
    private val exoProvider: Kohii by lazy { ExoProvider.get(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.galleryViewModel = galleryViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vpStory.offscreenPageLimit = 2

        exoProvider.register(this)
            .addBucket(binding.vpStory)

        /**Flags for transition from different modules-
         * 0->Incoming from Home Feed Module
         * 1->Incoming from Profile Module**/

        galleryViewModel.storyPos.observe(viewLifecycleOwner) {
            Log.d("MAP NEW", it.toString())
        }

        if (galleryViewModel.flagFlow.value == 0) galleryViewModel.prepareStoriesDisplay()
        binding.vpStory.adapter = adapter
        galleryViewModel.storiesDisplayList.observe(viewLifecycleOwner, adapter::submitItems)
        galleryViewModel.storyPos.observe(viewLifecycleOwner) {
            binding.vpStory.setCurrentItem(it, false)
        }

        binding.vpStory.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                galleryViewModel.setStoryPos(position)
            }
        })

        /** Sending User Back to the Profile Fragment.*/
        binding.profileStoryBackNavigationButton.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun getCurrentProfile(story: StoriesFetchModel): StoryProfileModel? {
        return if (galleryViewModel.commonStoryProfile.value == null) {
            /** not comming from profile */
            galleryViewModel.storyProfileMap.value?.get(story.id)
        } else {
            galleryViewModel.commonStoryProfile.value
        }
    }

    private fun openUserProfile(story: StoriesFetchModel) {
        Log.d("CHECK BEFORE", story.toString())

        /** navigate to their own profile section.*/
        if (story.id == baseViewModel.id.value) {
            (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.ProfileFlow)
            return
        }

        /**If the profile clicked is not the users profile
         * then , we will navigate them to the global public profile.
         */
        ProfileConstants.story = true
        ProfileConstants.storyProfileModel = StoryProfileModel(
            id = story.id,
            profilePicture = story.thumbnail,
            name = ""
        )
        Log.d("ProfileViewModel", "Sending : ${ProfileConstants.storyProfileModel.toString()}")
        (activity as ToFlowNavigable).navigateToFlow(
            NavigationFlow.GlobalProfileFlow
        )

    }

    override fun onImgOrTxtUsernameClicked(story: StoriesFetchModel) {

        if (galleryViewModel.commonStoryProfile.value == null) {
            openUserProfile(story)
            return
        }
        findNavController().navigateUp()

    }

    override fun onStop() {
        super.onStop()
        galleryViewModel.setCommonStoryProfile(null)
    }
}
