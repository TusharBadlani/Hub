package cessini.technology.profile.controller

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
//import cessini.technology.commonui.fragment.suggestion.categories
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.model.Video
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.video.VideoRepository
import cessini.technology.profile.R
import cessini.technology.profile.fragment.UserProfileVideo
import cessini.technology.profile.profileVideoRow
import cessini.technology.profile.viewmodel.ProfileViewModel
import com.airbnb.epoxy.AsyncEpoxyController
import kotlinx.coroutines.launch

class VideoController(
    var activity: FragmentActivity?,
    var viewModel: ProfileViewModel?,
    var userProfileVideoContext: UserProfileVideo?,
    private val videoRepository: VideoRepository,
    private val public: Boolean = false,
) : AsyncEpoxyController() {
    override fun buildModels() {
        allVideos.forEachIndexed { index, videoModel ->
            profileVideoRow {

                id(index)
                /**Setting up the video.*/
                videomodel(videoModel)
                thumbnail(videoModel.thumbnail)
                comments("")
                likes("")
                views(videoModel.viewCount.toString())
                if (videoModel.title.isEmpty()) {
                    videoTitleVisibilty(false)
                } else {
                    videoTitleVisibilty(true)
                    title(videoModel.title)
                }
                views("")

                delete(public)

                val a = ArrayAdapter.createFromResource(
                    activity!!,
                    R.array.video_option,
                    R.layout.spinner_list
                ).also {
                    it.setDropDownViewResource(R.layout.spinner_list)
                }

                adapter(a)
                val click = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 1) {
                            activity?.lifecycleScope?.launch {
                                videoRepository.delete(videoModel.id)
                            }
                            viewModel?.loadProfile(true)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

                selection(click)

                var duration = ""
                val minutes = videoModel.duration / 60
                val seconds = videoModel.duration % 60
                if (minutes < 10) {
                    duration = duration + "0" + minutes.toString()
                } else {
                    duration += minutes.toString()
                }

                duration += ":"

                if (seconds < 10) {
                    duration = duration + "0" + seconds.toString()
                } else {
                    duration += seconds.toString()
                }

                duration(duration.substring(1, duration.length))

                //          category(categories.find { it.id == videoModel.category }?.name.orEmpty())

                /**Perform action when user clicks on the video.*/
                onProfileVideoClicked { _, _, _, position ->

                    val galleryViewModel =
                        activity?.run {
                            ViewModelProvider(this)[GalleryViewModel::class.java]
                        } ?: throw Exception("Invalid Activity")

                    galleryViewModel.setVideoPos(position)
                    galleryViewModel.setVideoFlow(1)
                    galleryViewModel.setVideoDisplayList(
                        allVideos as ArrayList<Video>
                    )

                    Log.i(
                        "VideoController",
                        "Media : ${galleryViewModel.videoDisplayList.value}"
                    )

                    (userProfileVideoContext!!.requireActivity() as ToFlowNavigable).navigateToFlow(
                        NavigationFlow.GlobalVideoDisplayFlow
                    )
                }
            }
        }

    }

    /**Setting up the VideosList.*/
    var allVideos: MutableList<Video> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }
}
