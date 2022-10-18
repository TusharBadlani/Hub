package cessini.technology.explore.controller

//import cessini.technology.commonui.fragment.suggestion.categories
import android.content.Context
import android.util.Log
import android.widget.Filter
import android.widget.Filterable
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.cvo.exploremodels.CategoryModel
import cessini.technology.cvo.exploremodels.ProfileModel
import cessini.technology.cvo.exploremodels.SearchVideoModel
import cessini.technology.explore.userSearchVideoItem
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.model.search.VideoSearch
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.video.VideoRepository
import com.airbnb.epoxy.AsyncEpoxyController
import kotlinx.coroutines.launch
import java.util.*

class UserSearchVideoController(
    var context: Context,
    val activity: FragmentActivity,
    val viewModel: ExploreSearchViewModel,
    private val videoRepository: VideoRepository,
) : AsyncEpoxyController(), Filterable {

    var allVideos: MutableList<VideoSearch> = mutableListOf()

    val galleryViewModel by activity.viewModels<GalleryViewModel>()

    var videosFilterList = emptyList<VideoSearch>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        Log.e("VController", videosFilterList.toString())
        videosFilterList.forEachIndexed { index, searchVideoModel ->
            userSearchVideoItem {
                id(index)
                video(searchVideoModel)

                //video(searchVideoModel.copy(category = categories.find { it.id == searchVideoModel.category }?.name.orEmpty()))
                onClickVideo { _, _, _, position ->

                    //Toast.makeText(context, "Item no: $index", Toast.LENGTH_SHORT).show()
//                    ProfileConstants.videoModel = videosFilterList[position]

                    galleryViewModel.setVideoPos(position)
                    galleryViewModel.setVideoFlow(0)

                    val currentVideo = videosFilterList[position]
                    activity.lifecycleScope.launch {
                        runCatching {
                            val video = videoRepository.detail(currentVideo.id)

                            SearchVideoModel(
                                id = video.id,
                                title = video.title,
                                description = video.description,
                                category = CategoryModel(
                                    video.category.hashCode(),
                                    video.category.get(0)
                                ),
                                duration = video.duration,
                                thumbnail = video.thumbnail,
                                upload_file = video.url,
                                timestamp = video.timestamp.toString(),
                                profile = ProfileModel(
                                    id = video.profile.id,
                                    name = video.profile.name,
                                    channel_name = video.profile.channel,
                                    profile_picture = video.profile.picture,
                                )
                            )
                        }.onSuccess {
                            Log.e("ViderController", it.toString())
                            galleryViewModel.setVideoDisplay(it)
                            (activity as ToFlowNavigable).navigateToFlow(
                                NavigationFlow.GlobalVideoDisplayFlow
                            )
                        }
                    }

//                    Log.d(TAG , "Model Sent : ${ProfileConstants.videoModel}")


                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    videosFilterList = ArrayList(allVideos)
                } else {
                    val resultList = ArrayList<VideoSearch>()
                    for (row in allVideos) {
                        val query = row.title
                        if (query!!.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    videosFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = videosFilterList
                Log.i("Videos Filter List", videosFilterList.toString())
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                videosFilterList = results?.values as ArrayList<VideoSearch>
                Log.i("Videos Filter List", videosFilterList.toString())
                requestModelBuild()
            }

        }
    }
}
