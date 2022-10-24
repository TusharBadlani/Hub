package cessini.technology.camera.domain.controller

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.airbnb.epoxy.AsyncEpoxyController
import cessini.technology.camera.R
import cessini.technology.camera.galleryVideoRecyclerRow
import cessini.technology.commonui.presentation.HomeActivity
import cessini.technology.commonui.presentation.globalviewmodels.GalleryViewModel
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.cvo.homemodels.StoryModel
import java.io.File


class GalleryVideoController(var context: Activity?, var viewModel: GalleryViewModel?) :
    AsyncEpoxyController() {

    override fun buildModels() {
        allVideo.forEachIndexed { index, video ->
            galleryVideoRecyclerRow {
                id(index)
                /**Setting up the thumbnail of the video to the ImageView Using Data binding.*/
                galleryVideo(video)
                /**When User Click on A Video then,
                 * we send user to the Video Upload Fragment for uploading the selected video. */
                onVideoClicked { _ ->
                    onVideoClick(video)

                }
            }
        }
    }

    /**When a Video is selected from gallery to upload.*/
    private fun onVideoClick(path: String) {

        //fetching a file
        val videoFile = File(path)
        Log.d("GalleryVM", videoFile.name)
        //creating a thumbnail
        val bitmap = viewModel!!.setUpThumbnail(videoFile)


        if (viewModel!!.uploadType.value == 1) {
            Log.d("VideoGalleryFragment", "Path of Video: $path")

            viewModel!!.setVideo(VideoModel(bitmap, "", "", videoFile.path, ""))

            (context as HomeActivity).navController
                .navigate(R.id.action_videoGalleryFragment_to_videoUploadFragment)

        } else if (viewModel!!.uploadType.value == 0) {

            Log.d("VideoGalleryFragment", "Path of Viewer: $path")

            viewModel!!.setStory(StoryModel("", bitmap, 36, videoFile.path))

            (context as HomeActivity).navController
                .navigate(R.id.action_videoGalleryFragment_to_videoUploadFragment)

        } else {
            Toast.makeText(context, "Room not enabled", Toast.LENGTH_SHORT).show()
        }


    }

    /**Setting up the VideosList.*/
    var allVideo: ArrayList<String> =
        arrayListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

}