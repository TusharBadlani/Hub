package cessini.technology.home.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.cvo.homemodels.CommentModel
import cessini.technology.cvo.homemodels.StoriesFetchModel
import cessini.technology.cvo.homemodels.StoriesFetchResponse
import cessini.technology.home.model.HomeEpoxyStreamsModel
import cessini.technology.model.ProfileStories
import cessini.technology.model.Viewer
import cessini.technology.newrepository.authRepository.AuthRepository
import cessini.technology.newrepository.profileRepository.ProfileRepository
import cessini.technology.newrepository.story.StoryRepository
import cessini.technology.newrepository.video.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class HomeFeedViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val storyRepository: StoryRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeFeedViewModel"
    }

    // TODO: Remove this; ViewModels must not hold an activity reference
    /*lateinit var activity: Activity*/
//    var homeFeedRepository: HomeFeedRepository = HomeFeedRepository(application)
    var preVideoCount = MutableLiveData("0")
    var videoViewCount = MutableLiveData<String>("0")
    var storiesFetchList = MutableLiveData<ArrayList<StoriesFetchResponse>>()
    var homePos = MutableLiveData(0)
    var storyPos = MutableLiveData(0)
    var currVideoId = MutableLiveData("")
    var currStoryPos = MutableLiveData(0)
    var commentsList = MutableLiveData<ArrayList<CommentModel>>()
    var homeEpoxyStreamsList = MutableLiveData<MutableList<HomeEpoxyStreamsModel>>()

    lateinit var authEntity: AuthEntity

    var userId = MutableLiveData("")
    var username = MutableLiveData("")
    var profilePicture = MutableLiveData("")
    var email = MutableLiveData("")
    var channelName = MutableLiveData("")
    var authFlag = MutableLiveData(false)

    fun isUserSignedIn() {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                Log.d(TAG, "Checking if the user is signed in or not")
                authRepository.getAllAuth()
            }

            res.onSuccess {
                Log.d(TAG, "User is signed in, AuthList Size: ${it.size}")
                authEntity = it[0]
                authFlag.value = true
            }

            res.onFailure {
                Log.d(TAG, "User is not signed in")
                authEntity = AuthEntity("", "", "", "Newbie", "", "", "", "", "", "", "")
                authFlag.value = false
            }
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                Log.d(TAG, "Loading User Info")
                profileRepository.getUser()
            }

            res.onSuccess {
                Log.d(TAG, "User Info loaded, ${it.toString()}")
                if (it != null) {
                    authEntity = it
                }
                updateUserInfo()
            }

            res.onFailure {
                Log.d(TAG, "User info load failed")
            }
        }
    }

    private fun updateUserInfo() {
        userId.value = authEntity.id
        username.value = authEntity.displayName
        profilePicture.value = authEntity.photoUrl
        email.value = authEntity.email
        channelName.value = authEntity.channelName
    }

    fun loadHomeFeed() {
        viewModelScope.launch {
            runCatching {
                storiesFetchList.value = storyRepository.getStories()
                    .map { it.toModel() }.run { ArrayList(this) }
            }
        }
    }


    suspend fun countView(videoId: String): String {
        return runCatching { videoRepository.getViewAndDuration(videoId).views.toString() }
            .getOrDefault("0")
    }

    // post like on the video
    suspend fun postLikeOnVideo(id: String): Boolean {
        return runCatching {
            runCatching { videoRepository.like(id) }

            videoRepository.liked(id)
        }.getOrDefault(false)
    }

    /*fun goToAuth() {
        (activity as ToFlowNavigable).navigateToFlow(
            NavigationFlow.AuthFlow
        )
    }*/
}

private fun ProfileStories.toModel() = StoriesFetchResponse(
    id = profileId,
    name = "",
    profile_picture = picture,
    channel_name = "",
    stories = viewers.map { it.toModel() }.run { ArrayList(this) }
)

private fun Viewer.toModel() = StoriesFetchModel(
    id = profileId,
    caption = caption,
    thumbnail = thumbnail,
    duration = duration,
    upload_file = url,
    timestamp = timestamp.toString(),
)
