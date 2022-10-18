package cessini.technology.commonui.viewmodel.basicViewModels


import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.cvo.exploremodels.ProfileModel
import cessini.technology.cvo.exploremodels.SearchVideoModel
import cessini.technology.cvo.homemodels.StoriesFetchModel
import cessini.technology.cvo.homemodels.StoriesFetchResponse
import cessini.technology.cvo.homemodels.StoryModel
import cessini.technology.cvo.homemodels.StoryResponse
import cessini.technology.cvo.profileModels.StoryProfileModel
import cessini.technology.model.Video
import cessini.technology.newrepository.cameraRepository.VideoGalleryRepository
import cessini.technology.newrepository.story.StoryRepository
import cessini.technology.newrepository.video.VideoRepository
import cessini.technology.newrepository.websocket.video.VideoViewUpdaterWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val videoGalleryRepository: VideoGalleryRepository,
    private val videoRepository: VideoRepository,
    private val storyRepository: StoryRepository,
    private val viewsUpdater: VideoViewUpdaterWebSocket
) : ViewModel() {

    companion object {
        private const val TAG = "GalleryViewModel"
    }

    private val _videoViewCount = MutableLiveData<String>()
    val videoViewCount: LiveData<String> get() = _videoViewCount

    private val _uploadType = MutableLiveData(1)
    val uploadType: LiveData<Int> get() = _uploadType

    private val _pathListVideo = MutableLiveData<ArrayList<String>>()
    val pathListVideo: LiveData<ArrayList<String>> get() = _pathListVideo

    private val _video = MutableLiveData<VideoModel>()
    val video: LiveData<VideoModel> get() = _video

    private val _story = MutableLiveData<StoryModel>()
    val story: LiveData<StoryModel> get() = _story

    //TODO(Check for two way databinding issue caused when adding livedata getter in fragment_video_upload line 135)
    val videoTitle = MutableLiveData<String>()
    val videoDescription = MutableLiveData<String>()

    private val _videoCategory = MutableLiveData<String>()
    val videoCategory: LiveData<String> get() = _videoCategory

    private val _videoModelArrList = MutableLiveData<ArrayList<VideoModel>>()
    val videoModelArrList: LiveData<ArrayList<VideoModel>> get() = _videoModelArrList

    private val _currVideoId = MutableLiveData(0)
    val currVideoId: LiveData<Int> get() = _currVideoId

    private val _storyModel = MutableLiveData<StoryResponse>()
    val storyModel: LiveData<StoryResponse> get() = _storyModel

    private val _uploadProgress = MutableLiveData(-4)
    val uploadProgress: LiveData<Int> get() = _uploadProgress

    private val _oldFileUri = MutableLiveData<String>()
    val oldFileUri: LiveData<String> get() = _oldFileUri

    private val _storyPos = MutableLiveData(0)
    val storyPos: LiveData<Int> get() = _storyPos

    private val _storySugPos = MutableLiveData(0)
    val storySugPos: LiveData<Int> get() = _storySugPos

    private val _storiesFetchList = MutableLiveData<ArrayList<StoriesFetchResponse>>()
    val storiesFetchList: LiveData<ArrayList<StoriesFetchResponse>> get() = _storiesFetchList

    private val _storiesDisplayList = MutableLiveData<ArrayList<StoriesFetchModel>>()
    val storiesDisplayList: LiveData<ArrayList<StoriesFetchModel>> get() = _storiesDisplayList

    private val _storyProfileMap = MutableLiveData<HashMap<String?, StoryProfileModel>>()
    val storyProfileMap: LiveData<HashMap<String?, StoryProfileModel>> get() = _storyProfileMap

    private val _storyPosMap = MutableLiveData<HashMap<Int, Int>>()
    val storyPosMap: LiveData<HashMap<Int, Int>> get() = _storyPosMap

    private val _videoDisplayList = MutableLiveData<ArrayList<Video>>()
    val videoDisplayList: LiveData<ArrayList<Video>> get() = _videoDisplayList

    private val _commonVideoProfile = MutableLiveData<ProfileModel?>(null)
    val commonVideoProfile: LiveData<ProfileModel?> get() = _commonVideoProfile

    private val _commonStoryProfile = MutableLiveData<StoryProfileModel?>(null)
    val commonStoryProfile: LiveData<StoryProfileModel?> get() = _commonStoryProfile

    private val _videoDisplay = MutableLiveData<SearchVideoModel>()
    val videoDisplay: LiveData<SearchVideoModel> get() = _videoDisplay

    private val _videoPos = MutableLiveData(0)
    val videoPos: LiveData<Int> get() = _videoPos

    private val _flagFlow = MutableLiveData(0)
    val flagFlow: LiveData<Int> get() = _flagFlow

    private val _videoFlow = MutableLiveData(0)
    val videoFlow: LiveData<Int> get() = _videoFlow

    var isOpenFirstTime = true

    /** List of Simple ExoPlayer Instance.*/

    /** Get all the image from the storage and return their URI as a string .*/
    fun loadVideoPathList(context: Context) {
        viewModelScope.launch {

            val result = kotlin.runCatching {
                videoGalleryRepository.loadVideoPathList(context)
            }

            result.onSuccess {
                Log.d(TAG, "Gallery Load Successful: ${it.size}")
                _pathListVideo.value = it
            }

            result.onFailure {
                Log.d(TAG, "Gallery Load Failed: ${it.message}")
            }

        }

    }

    // post like on the video
    fun postLikeOnVideo(videoId: String) {
        viewModelScope.launch {
            videoRepository.like(videoId)
        }
    }

    fun setUpThumbnail(file: File): Bitmap? {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(file, Size(180, 200), CancellationSignal())
            } else {
                ThumbnailUtils.createVideoThumbnail(
                    file.absolutePath,
                    MediaStore.Video.Thumbnails.MINI_KIND,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    suspend fun getUser() {
//        val result = kotlin.runCatching {
//            profileRepository.getUser()
//        }
//
//        result.onSuccess {
//            Log.d(TAG, "Received from DB: $it")
//        }
//
//        result.onFailure {
//
//            Log.d(TAG, "Get user FAILURE ${it.message}")
//            //getUserApi()
//            goToAuth()
//        }
//
//    }


    fun insertNewVideo(authFlag: Boolean) {
        _uploadProgress.value = if (authFlag) -2 else -3
    }

    fun insertVideoInAPI(context: Context) {
        _uploadProgress.value = 0
        video.value?.videoDesc = videoDescription.value ?: ""
        video.value?.videoTitle = videoTitle.value ?: ""
        // video.value?.videoCategory = categories.find { it.name == videoCategory.value }?.id ?: "1"
        Log.d("VideoUploadFragment", video.value?.videoCategory.toString())

        val thumbnailName =
            video.value?.videoTitle + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"
        val thumbnailFile =
            video.value?.videoThumbnail?.let { getThumbnail(context, it, thumbnailName) }

        Log.d("VideoUploadFragment", video.value.toString())

        viewModelScope.launch {
            val result = kotlin.runCatching {
                val video = video.value!!
                videoRepository.upload(
                    title = video.videoTitle,
                    description = video.videoDesc,
                    category = video.videoCategory,
                    video = video.videoUrl,
                    thumbnail = thumbnailFile!!
                )
            }
            result.onSuccess {
                Log.i(TAG, "Video inserted in API: $it")
                _uploadProgress.value = 100
            }
            result.onFailure {
                Log.i(TAG, "Video not inserted in API ${it.message}")
                _uploadProgress.value = -1
            }
        }

    }

    /** Function to Upload Viewer.*/
    fun insertStoryInAPI(context: Context) {


        _uploadProgress.value = 0
        story.value?.caption = videoTitle.value ?: ""

        val thumbnailName =
            story.value?.caption + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"
        val thumbnailFile = story.value?.thumbnail?.let { getThumbnail(context, it, thumbnailName) }

        Log.d("CHECK", thumbnailFile.toString())



        viewModelScope.launch {
            val result = kotlin.runCatching {
                storyRepository.upload(
                    caption = story.value!!.caption.orEmpty(),
                    story = story.value!!.upload_file.orEmpty(),
                    thumbnail = thumbnailFile.orEmpty(),
                )
            }

            result.onSuccess {
                Log.d(TAG, "API was successful with response : $it")
                _uploadProgress.value = 100
            }

            result.onFailure {
                Log.d(TAG, "API call failed due to : $it")
                _uploadProgress.value = -1
            }
        }
    }


    /**Function to get the Duration of the Video File.*/
    fun getDuration(file: File): String? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        Log.d(TAG, "Duration of ${file.name} is : $duration")
        retriever.release()

        return duration
    }

    /**Function to generate the Thumbnail.*/
    private fun getThumbnail(context: Context, bitmap: Bitmap, fileName: String): String {
        /** Setting up the root path for the directory.*/
        val root = context.getExternalFilesDir("/Videos")

        /** Creating my own Directory in Storage.*/
        val myDirectory = File("$root")
        myDirectory.mkdirs()

        /**Initializing a new file.*/
        val file = File(myDirectory, fileName)

        /**If the File with the same Name is Present ,
         * Then , Delete the old file.
         */
        if (!oldFileUri.value.isNullOrEmpty()) {
            val oldFile = oldFileUri.value?.let { File(it) }
            Log.d(TAG, "Old File at : $oldFile")
            if (oldFile?.exists() == true) {
                Log.d(TAG, "File : $oldFile exists.")
                if (oldFile.delete()) {
                    Log.d(TAG, "Old File at $oldFile deleted successfully.")
                } else {
                    Log.d(TAG, "Old File at $oldFile failed to delete.")
                }
            } else {
                Log.d(TAG, "File : $oldFile does not exists.")
            }
        }
        /** Creating a new file.*/
        file.createNewFile()

        _oldFileUri.value = file.absolutePath

        Log.d(TAG, "File created at : ${file.absolutePath}")

        try {
            Log.d(TAG, "FileOutputStream has started.")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            Log.d(TAG, "FileOutputStream has ended.")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file.absolutePath
    }


    fun callMuxUtil() {
//        var audioRepository = AudioRepository(context)
//        var songInfo: SongInfo? = null
//        val res = kotlin.runCatching {
//            var audio = audioRepository.fetchMusicFromAPI()
//            songInfo = audio[1].audio[0]
//        }
//
//        res.onSuccess {
////            var muxUtil = MuxUtil(context, File(video.value?.videoUrl!!), File(songInfo?.songURL!!))
////            muxUtil.muxMethod()
//        }


    }

    fun prepareStoriesDisplay() {


        /** don't change the value if it is not the first load from homefeed */
        if (storyPos.value != 0) {
            return
        }


        /** continue if it is first load from homefeed */
        val storyList = arrayListOf<StoriesFetchModel>()
        var idx = 0

        /**
         *  0 A -> 0      1,2,3
         *  1 B -> 3      4,5
         *  2 C -> 5      6
         *  3 D -> 6      7, 8
         *
         *  storyList -> 1 2 3 4 5 6 7 8
         *
         */


        val profMap: HashMap<String?, StoryProfileModel> = HashMap()
        val posMap = HashMap<Int, Int>()
        var c = 0
        for ((pidx, parent) in storiesFetchList.value!!.withIndex()) {

            posMap[pidx] = c
            for (e in parent.stories!!) {
                storyList.add(e)
                profMap[e.id] = StoryProfileModel(parent.id, parent.profile_picture, parent.name)
                c++
            }

            idx += parent.stories!!.size
        }

        _storyProfileMap.value = profMap
        _storyPosMap.value = posMap

        _storyPos.value = storyPosMap.value?.get(storySugPos.value!!)

        Log.d("MAP", storyPos.value.toString())
        _storiesDisplayList.value = storyList

    }

    fun setStoryPos(position: Int) {
        _storyPos.value = position
    }

    fun setCommonStoryProfile(storyProfileModel: StoryProfileModel?) {
        _commonStoryProfile.value = storyProfileModel
    }

    fun setCommonVideoProfile(profileModel: ProfileModel?) {
        _commonVideoProfile.value = profileModel
    }

    fun setVideoPos(position: Int) {
        _videoPos.value = position
    }

    fun setVideoFlow(value: Int) {
        _videoFlow.value = value
    }

    fun setVideoDisplayList(videos: ArrayList<Video>) {
        _videoDisplayList.value = videos
    }

    fun setFlagFlow(value: Int) {
        _flagFlow.value = value
    }

    fun setStorySugPos(position: Int) {
        _storySugPos.value = position
    }

    fun setStoriesFetchList(value: ArrayList<StoriesFetchResponse>?) {
        _storiesFetchList.value = value
    }

    fun setVideoDisplay(searchVideoModel: SearchVideoModel) {
        _videoDisplay.value = searchVideoModel
    }

    fun setUploadProgress(value: Int) {
        _uploadProgress.value = value
    }

    fun setVideoTitle(title: String) {
        videoTitle.value = title
    }

    fun setVideoCategory(category: String) {
        _videoCategory.value = category
    }

    fun setVideo(video: VideoModel?) {
        _video.value = video
    }

    fun clearVideo() {
        videoTitle.value = null
        videoDescription.value = null
        _videoCategory.value = null
        _video.value = null
        _story.value = null
    }

    fun setStory(storyModel: StoryModel) {
        _story.value = storyModel
    }

    fun setUploadType(value: Int) {
        _uploadType.value = value
    }

    fun getVideoCount(videoId: String) {
        viewModelScope.launch {
            _videoViewCount.value = runCatching {
                videoRepository.getViewAndDuration(videoId).views.toString()
            }.getOrDefault("0")
        }
    }

    fun getLikedStatus(videoId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback.invoke(videoRepository.liked(videoId))
        }
    }

    fun updatePlayerOnPaused(videoId: String, duration: Int) {
        viewsUpdater(videoId, duration)
    }

}
