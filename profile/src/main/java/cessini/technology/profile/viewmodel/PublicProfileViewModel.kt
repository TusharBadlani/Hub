package cessini.technology.profile.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.services.sns.SnsClient
import cessini.technology.commonui.AmazonSNSImpl
import cessini.technology.commonui.UserArn
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.cvo.profileModels.ProfileStoryModel
import cessini.technology.cvo.profileModels.ProfileVideoModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.datastores.ProfileStoreKeys.followingCount
import cessini.technology.newrepository.myworld.FollowRepository
import cessini.technology.newrepository.profileRepository.ProfileRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import cessini.technology.newrepository.myworld.ProfileRepository as NewProfileRepository

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val newProfileRepository: NewProfileRepository,
    private val followRepository: FollowRepository,
    private val profileRepository: ProfileRepository,
    private val amazonSNSImpl: AmazonSNSImpl
) : ViewModel() {
    companion object {
        private const val TAG = "PublicProfileViewModel"
    }

//    lateinit var currentId: String
var currentId : String ="ID825560790000000104"
//var currentId : String ="ID335167180000000097"

    var channelName = MutableLiveData<String>()
    var displayName = MutableLiveData<String>()
    var followersCount = MutableLiveData<String>("00")
    var followingCount = MutableLiveData<String>("00")
    var followingStatus = MutableLiveData<Boolean>()
    var verified = MutableLiveData<Boolean>()
    var followingText = MutableLiveData<String>("Follow")
    var photoUrl = MutableLiveData<String>()
    var bio = MutableLiveData<String>("")
    var profileVideoListAPI = MutableLiveData<ArrayList<ProfileVideoModel>>()
    var profileStoriesListAPI = MutableLiveData<ArrayList<ProfileStoryModel>>()

    /**For the Video Adapter.*/
    var storiesList = MutableLiveData<ArrayList<VideoModel>>()
    var storyPosition = MutableLiveData(0)

    var profileLoadProgress = MutableLiveData<Int>(0)

    var messageProgress = MutableLiveData(-3)

    var deviceToken : String? = null

    private lateinit var profileEntity: AuthEntity

    lateinit var activity: Activity

    fun loadProfile(choice: Boolean) {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                getProfilePageData(choice)
            }
            result.onSuccess {
                profileLoadProgress.value = 100
                Log.i(TAG, "Profile Page Load Successful")
            }
            result.onFailure {
                profileLoadProgress.value = -1
                Log.i(TAG, "Profile Page Load Failed ${it.message}")
//                goToAuth()
            }
        }
    }


    suspend fun getProfilePageData(choice: Boolean) {
        val res1 = kotlin.runCatching {
            getProfileDataFromSearch(choice)
        }

        res1.onFailure {
            Log.d(TAG, "User Data load failed ${it.message}")


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
                goToAuth()
            }
        }

        result.onFailure {

            Log.d(TAG, "Get user FAILURE ${it.message}")
        }

    }


    private fun updateUIData() {
        Log.d(TAG,"Inside Public profile vm updateUIData()")
        Log.i(TAG, profileEntity.toString())
        // function to update data members of this class and update the UI
        displayName.value = profileEntity.displayName
        channelName.value = profileEntity.channelName
        bio.value = profileEntity.bio
        photoUrl.value = profileEntity.photoUrl

        Log.d(TAG, "Followers: ${followersCount.value}, Following: ${followingCount.value}")
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

    fun onFollowClick(view: View) {
        if ((this.activity as HomeActivity).baseViewModel.authFlag.value == true) {

            if (followingStatus.value == true) {
                unfollowUser()
                Log.e("Follow","Unfollowed")
                followingStatus.value = false
            } else {
                followUser()
                viewModelScope.launch {
                    amazonSNSImpl.sendFollowNotification(profileEntity.id)
                }
                Log.e("Follow","Followed")
                followingStatus.value = true
            }


        } else {
            goToAuth()
        }

    }

    fun onFollowClickExplore(id: String) {
        if ((this.activity as HomeActivity).baseViewModel.authFlag.value == true) {
            fetchPublicProfileInfoAPIAuthExplore(id)
            Log.e("FollowingStatus",followingStatus.value.toString())
            if (followingStatus.value == true) {
                unfollowUserExplore(id)
                Log.e("Follow","Unfollowed")
                followingStatus.value = false
            } else {
                followUserExplore(id)
                Log.e("Follow","Followed")
                followingStatus.value = true
            }


        } else {
            goToAuth()
        }

    }

    fun goToAuth() {
        (activity as ToFlowNavigable).navigateToFlow(
            NavigationFlow.AuthFlow
        )
    }


    private fun checkNull(value: String?): String {
        if (value == null) {
            return ""
        }

        return value
    }

    fun getProfileDataFromSearch(choice: Boolean) {
        profileEntity = if (choice) {
            val storyModel = ProfileConstants.storyProfileModel!!
            Log.d("Choice:$choice ProfileResponse", storyModel.toString())
            AuthEntity(
                id = checkNull(storyModel.id.toString())
            )
        } else {
            val creatorModel: SearchCreatorApiResponse = ProfileConstants.creatorModel!!
            Log.d("Choice:$choice ProfileResponse", creatorModel.toString())
            AuthEntity(
                id = checkNull(creatorModel.id.toString()),
                displayName = checkNull(creatorModel.name.toString()),
                email = checkNull(creatorModel.channel_name),
                photoUrl = checkNull(creatorModel.profile_picture)
            )
        }
        if ((activity as HomeActivity).baseViewModel.authFlag.value == true) {
            fetchPublicProfileInfoAPIAuth()
        } else {
            fetchPublicProfileInfoAPI()
        }

    }

    fun askUserToCreateRoom(message: String, userId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                newProfileRepository.sendMessageToUser(message, userId)
            }
                .onSuccess {
                    if(it) {
                        messageProgress.value = 0
                    } else {
                        messageProgress.value = -1
                    }
                }
                .onFailure {
                    messageProgress.value = -2
                    it.printStackTrace()
                }
        }
    }


    private fun fetchPublicProfileInfoAPI() {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                newProfileRepository.getProfile(profileEntity.id)
            }

            res.onSuccess {
                Log.e("PublicProfile", "Response : ${it}")
                currentId = it.id
                profileEntity.displayName = it.name ?: "User name"
                profileEntity.bio = it.bio ?: ""
                profileEntity.channelName = it.channelName ?: "Channel Name"
                profileEntity.photoUrl = it.profilePicture ?: ""

                /**No entry in database for these**/
                followingCount.value = (it.followingCount).toString()
                followersCount.value = (it.followerCount).toString()
                followingStatus.value = it.following
                verified.value=it.verified
                updateUIData()
            }
        }
    }

    /**Function to follow the user.*/
    fun followUser() {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                Log.d(TAG, "Attempting to follow ${profileEntity.id}")
                followRepository.followUser(profileEntity.id)
            }

            result.onSuccess {
                Log.d(TAG, "${profileEntity.id} has been followed Successfully.")
                fetchPublicProfileInfoAPIAuth()
            }

            result.onFailure {
                Log.d(TAG, "Follow User API called failed due to ${it.message}.")
            }
        }
    }

    /**Function to unfollow the user.*/
    fun unfollowUser() {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                Log.d(TAG, "Attempting to unfollow ${profileEntity.id}")
                followRepository.unFollowUser(profileEntity.id)
            }

            result.onSuccess {
                Log.d(TAG, "${profileEntity.id} has been unfollowed Successfully.")
                fetchPublicProfileInfoAPIAuth()
            }

            result.onFailure {
                Log.d(TAG, "Unfollow User API called failed due to ${it.message}.")
            }
        }
    }

    fun followUserExplore(id: String) {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                Log.d(TAG, "Attempting to follow ${id}")
                followRepository.followUser(id)
            }

            result.onSuccess {
                Log.d(TAG, "${id} has been followed Successfully.")
                fetchPublicProfileInfoAPIAuthExplore(id)
            }

            result.onFailure {
                Log.d(TAG, "Follow User API called failed due to ${it.message}.")
            }
        }
    }

    /**Function to unfollow the user.*/
    fun unfollowUserExplore(id:String) {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                Log.d(TAG, "Attempting to unfollow ${id}")
                followRepository.unFollowUser(id)
            }

            result.onSuccess {
                Log.d(TAG, "${id} has been unfollowed Successfully.")
                fetchPublicProfileInfoAPIAuthExplore(id)
            }

            result.onFailure {
                Log.d(TAG, "Unfollow User API called failed due to ${it.message}.")
            }
        }
    }


    private fun fetchPublicProfileInfoAPIAuth() {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                newProfileRepository.getProfileWithFollowingStatus(profileEntity.id)
            }

            res.onSuccess {
                currentId = it.id
                profileEntity.displayName = it.name ?: "User name"
                profileEntity.bio = it.bio ?: ""
                profileEntity.channelName = it.channelName ?: "Channel Name"
                profileEntity.photoUrl = it.profilePicture ?: ""

                /**No entry in database for these**/
                Log.e(TAG, "API RESPONSE $it")
                followingCount.value = (it.followingCount).toString()
                followersCount.value = (it.followerCount).toString()
                followingStatus.value = it.following!!
                verified.value=it.verified
                updateUIData()
            }

            res.onFailure {
                Log.d(TAG, it.message.toString())
            }

        }

    }

    private fun fetchPublicProfileInfoAPIAuthExplore(id: String) {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                newProfileRepository.getProfileWithFollowingStatus(id)
            }

            res.onSuccess {
//                currentId = it.id
//                profileEntity.displayName = it.name ?: "User name"
//                profileEntity.bio = it.bio ?: ""
//                profileEntity.channelName = it.channelName ?: "Channel Name"
//                profileEntity.photoUrl = it.profilePicture ?: ""

                /**No entry in database for these**/
                Log.e(TAG, "API RESPONSE $it")
                followingCount.value = (it.followingCount).toString()
                followersCount.value = (it.followerCount).toString()
                followingStatus.value = it.following!!
                verified.value=it.verified
                //updateUIData()
            }

            res.onFailure {
                Log.d(TAG, it.message.toString())
            }

        }

    }

    fun clearUserInfo() {
        channelName.value = ""
        displayName.value = ""
        followingCount.value = ""
        followersCount.value = ""
        followingStatus.value = false
        followingText.value = "Follow"
        photoUrl.value = ""
        bio.value = ""
        profileVideoListAPI.value?.clear()
        verified.value=false
    }

    fun updateFollowingStatus(id:String){
        fetchPublicProfileInfoAPIAuthExplore(id)
    }

}
