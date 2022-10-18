package cessini.technology.profile.viewmodel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.commonui.utils.networkutil.NetworkUtils
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.model.*
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.explore.ExploreRepository
import cessini.technology.newrepository.explore.RegistrationRepository
import cessini.technology.newrepository.explore.VideoCategoryRepository
import cessini.technology.newrepository.profileRepository.ProfileRepository
import cessini.technology.newrepository.utils.Resource
import cessini.technology.profile.fragment.profileVideo.ProfileVideoFragment
import com.bumptech.glide.Glide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject
import cessini.technology.newrepository.myworld.ProfileRepository as NewProfileRepository

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val newProfileRepository: NewProfileRepository,
    private val profileRepository: ProfileRepository,
    private val exploreRepository: ExploreRepository,
    private val registrationRepository: RegistrationRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
    private val application: Application
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    val profile = newProfileRepository.profile

    val rooms = MutableLiveData<List<Room>>(null)
    val allRecordedVideo = MutableLiveData<RecordedVideo>(null)
    val messages = MutableLiveData<List<CreateRoomRequest>>()
//    private var profileRepository: ProfileRepository = ProfileRepository(application)
    var channelName = MutableLiveData<String>()
    var displayName = MutableLiveData<String>()
    var photoUrl = MutableLiveData<String>()
    var bio = MutableLiveData<String>("")
    var userId: String = ""
    var profileVideoList = MutableLiveData<ArrayList<VideoModel>>()
    var profileVideoListAPI = MutableLiveData<ArrayList<Video>>()
    var profileStoriesListAPI = MutableLiveData<ArrayList<Viewer>>(null)

    var publicProfilerofileVideoListAPI = MutableLiveData<ArrayList<Video>>()
    var publicProfileStoriesListAPI = MutableLiveData<ArrayList<Viewer>>(null)


    /**For the Video Adapter.*/
    var storiesList = MutableLiveData<ArrayList<VideoModel>>()
    var storyPosition = MutableLiveData(0)

    var bitmap = MutableLiveData<Bitmap?>()

    var profileLoadProgress = MutableLiveData<Int>(0)

    var upiSaveProgress = MutableLiveData(-3)

    private lateinit var profileEntity: AuthEntity

    private val _upiData = MutableLiveData<String>()
    val upiData: LiveData<String> = _upiData

    // FIXME: Remove this activity reference
    lateinit var activity: Activity

    fun isNetworkAvailable(): Boolean{
        return NetworkUtils.isInternetAvailable(application)
    }

    fun loadProfile(choice: Boolean) {
        viewModelScope.launch {
            val result = kotlin.runCatching { getProfilePageData(choice) }
            result.onSuccess {
                profileLoadProgress.value = 100
                Log.i(TAG, "Profile Page Load Successful")
            }
            result.onFailure {
                profileLoadProgress.value = -1
                Log.i(TAG, "Profile Page Load Failed ${it.message}")
                goToAuth()
            }
        }
    }


    private suspend fun getProfilePageData(choice: Boolean) {
        if (!ProfileConstants.public) {
            val res1 = kotlin.runCatching {
                getUser()
            }

            res1.onFailure {
                Log.d(TAG, "User Data load failed ${it.message}")


            }
            val res2 = kotlin.runCatching {
                val profile_id = (activity as HomeActivity).baseViewModel.id.value
                getProfileMedia(profile_id = profile_id.toString())
            }

            res2.onFailure {
                Log.d(TAG, "Video Data load failed ${it.message}")
            }
        } else {
            var profile_id = if (choice) {
                ProfileConstants.storyProfileModel!!.id.toString()
            } else {
                ProfileConstants.creatorModel!!.id.toString()
            }
            val res3 = kotlin.runCatching {
//                val profile_id = ProfileConstants.creatorModel!!.id
                Log.d(TAG, "Receiving : ${profile_id}")
                getPublicProfileMedia(profile_id = profile_id)
            }

            res3.onFailure {
                Log.d(TAG, "Profile Media load failed ${it.message}")
            }
        }

    }

    suspend fun getUser() {
        val result = kotlin.runCatching {
            profileRepository.getUser()
        }

        result.onSuccess {
            Log.d(TAG, "Received from DB: $it")
            if (it != null) {
                profileEntity = it
                updateUIData()
            } else {
                //getUserApi()
                goToAuth()
            }
        }

        result.onFailure {

            Log.d(TAG, "Get user FAILURE ${it.message}")
            //getUserApi()
        }

    }

    fun sendUserId():String{
        return profileEntity.id
    }



    private fun updateUIData() {
        // function to update data members of this class and update the UI
        Log.d(TAG,"Inside updateUIData")
        displayName.value = profileEntity.displayName
        channelName.value = profileEntity.channelName
        photoUrl.value = profileEntity.photoUrl
        bio.value = profileEntity.bio
        userId = profileEntity.id
    }

    private fun updateDB() {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                profileRepository.updateDB(profileEntity)
            }

            result.onSuccess {
                Log.d(TAG, "DB is updated: $it")
            }

            result.onFailure {
                Log.d(TAG, "DB update FAILURE ${it.message}")
            }
        }
    }

    fun goToAuth() {
        (activity as ToFlowNavigable).navigateToFlow(
            NavigationFlow.AuthFlow
        )
    }


    /** Function to return Bitmap from path.*/
    fun getBitmap(url: String, context: ProfileVideoFragment): Bitmap? {
        Log.d(TAG, "$url")
        viewModelScope.launch {
            val result = kotlin.runCatching {
                Glide.with(context).asBitmap().load(url).into(100, 100).get()
            }
            result.onSuccess {
                bitmap.value = it
            }
        }
        return bitmap.value
    }

    suspend fun getProfileMedia(profile_id: String) {

        val resultRecordedVideo = exploreRepository.getRecordedVideosProfile(profile_id)
        resultRecordedVideo.collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    allRecordedVideo.value = result.data
                    Log.e(TAG,"Recorded Videos Fetched")
                }
                is Resource.Error -> {
                    Log.e(TAG,"Failed to fetch Recorded Videos ${result.data}")
                }
            }
        }


        val result = kotlin.runCatching {
            newProfileRepository.getProfileMedia(profile_id).apply {
                Log.d(TAG, "getProfileMedia during call: ${this.videos}")
            }
        }
        result.onSuccess {
            Log.i(TAG, "Videos and viewers fetched Profile: $it")
            var temp = it
            temp.rooms?.forEach {
                if(!it.listeners.isNullOrEmpty())
                it.listeners = it.listeners.toSet().toList()
            }
            rooms.value = temp.rooms!!
            Log.e(TAG, "Room: ${it.rooms}")
            profileVideoListAPI.value = ArrayList(it.videos)
            profileStoriesListAPI.value = ArrayList(it.viewers)

//            var r1 = Room("1","Room1","Title1", Creator("11","Tabish","",""),10,false,false, emptyList(), emptyList())
//            var r2 = Room("1","Room1","Title1", Creator("11","Tabish","",""),10,false,false, emptyList(), emptyList())
//            rooms.value = listOf(r1,r2)

        }
        result.onFailure {
            Log.i(TAG, "Video and viewers not fetched in private media: ${it.message}")
        }

    }

    suspend fun getPublicProfileMedia(profile_id: String) {


        val resultRecordedVideo = exploreRepository.getRecordedVideosProfile(profile_id)
        resultRecordedVideo.collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    allRecordedVideo.value = result.data
                    Log.e(TAG,"Recorded Videos Fetched")
                }
                is Resource.Error -> {
                    Log.e(TAG,"Failed to fetch Recorded Videos ${result.data}")
                }
            }
        }


        val result = kotlin.runCatching {
            newProfileRepository.getProfileMedia(profile_id)
        }
        result.onSuccess {
            Log.i(TAG, "Videos and viewers fetched Public: $it")
            var temp = it
            temp.rooms?.forEach {
                if(!it.listeners.isNullOrEmpty())
                    it.listeners = it.listeners.toSet().toList()
            }
            rooms.value = temp.rooms!!
            Log.e(TAG, "Room: ${it.rooms}")
            publicProfilerofileVideoListAPI.value = ArrayList(it.videos)
            publicProfileStoriesListAPI.value = ArrayList(it.viewers)
//            var r1 = Room("1","Room1","Title1", Creator("11","Tabish","",""),10,false,false, emptyList(), emptyList())
//            var r2 = Room("1","Room1","Title1", Creator("11","Tabish","",""),10,false,false, emptyList(), emptyList())
//            rooms.value = listOf(r1,r2)
        }
        result.onFailure {
            Log.i(TAG, "Video and viewers not fetched Public: ${it.message}")
        }

    }

    fun clearPublicUserInfo() {
        publicProfilerofileVideoListAPI.value?.clear()
        publicProfileStoriesListAPI.value?.clear()
    }

    fun saveUPIData(upiId:String, upiName: String, isFirstTime: Boolean) {
        val regex = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE)
        val matcher = regex.matcher(upiId)
        if(!matcher.find()) {
            upiSaveProgress.value = -1
            return
        }
        viewModelScope.launch {
            upiSaveProgress.value = 0
            val result = if(isFirstTime) {
                kotlin.runCatching { newProfileRepository.saveUPIData(userId, upiId, upiName) }
            } else kotlin.runCatching {
                newProfileRepository.updateUPIData(upiId, upiName)
            }
            result.onSuccess {
                if(it)
                    upiSaveProgress.value = 100
                else
                    upiSaveProgress.value = -2
            }
            result.onFailure {
                upiSaveProgress.value = -2
            }
        }
    }

    fun getCreatorUPIData(creatorId: String) {
        viewModelScope.launch {
            kotlin.runCatching { newProfileRepository.getCreatorUPIData(creatorId) }
                .onSuccess {
                    _upiData.value = "${it.upiId}^${it.upiName}"
                }
                .onFailure {
                    Log.d(TAG, it.stackTraceToString())
                }
        }
    }

    suspend fun getMessageForRoom() = newProfileRepository.getMessageForRoom()

    suspend fun registerCategoriesAfterLogin(
        userId: String
    ) = registrationRepository.registerCategoriesAfterLogin(
        userId,
        videoCategoryRepository.categoryIds.first(),
        videoCategoryRepository.subCategoryIds.first(),
    )

    /**Function to clear all fields after user log out.*/
    fun clearProfileDataAfterLogOut() {
        profileStoriesListAPI.value = arrayListOf()
        profileVideoListAPI.value = arrayListOf()
    }
}
