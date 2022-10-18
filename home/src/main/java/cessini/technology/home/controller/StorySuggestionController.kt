package cessini.technology.home.controller

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.cvo.homemodels.StoriesFetchResponse
import cessini.technology.home.circularVideoSuggestionRecyclerItem
import cessini.technology.home.fragment.StoriesFragment
import cessini.technology.home.viewmodel.HomeFeedViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import com.airbnb.epoxy.AsyncEpoxyController

class StorySuggestionController(
    val viewModel: HomeFeedViewModel?,
    val context: StoriesFragment?
) : AsyncEpoxyController() {

    companion object {
        private const val TAG = "StorySuggestionContro"
    }

    lateinit var galleryViewModel: GalleryViewModel

    override fun buildModels() {
        allStories.forEachIndexed { index, storiesFetchResponse ->

            circularVideoSuggestionRecyclerItem {
                id(index)
                setStorySuggestionThumbnail(storiesFetchResponse.profile_picture)

                Log.d(TAG, "Size: ${allStories.size}")

                onStoryClicked { _, _, _, position ->

                    Log.d(TAG, "On CLick : ${allStories[position]}")

                    //(TODO) Use DI instead if possible
                    val galleryViewModel =
                        ViewModelProvider(context!!.requireActivity())[GalleryViewModel::class.java]
                    galleryViewModel.setFlagFlow(0)
//                    galleryViewModel.storyPos.value = position
                    galleryViewModel.setStorySugPos(position)
                    galleryViewModel.setStoriesFetchList(viewModel?.storiesFetchList?.value)

                    Log.i(
                        TAG,
                        "Media : ${galleryViewModel.storiesFetchList.value}"
                    )

                    (context.requireActivity() as ToFlowNavigable).navigateToFlow(
                        NavigationFlow.GlobalStoryDisplayFlow
                    )

                }
            }
        }
    }

    /**Setting up the viewers for the suggestion.*/
    var allStories: MutableList<StoriesFetchResponse> =
        emptyList<StoriesFetchResponse>().toMutableList()
        set(value) {
            field = value
            requestModelBuild()
        }
}