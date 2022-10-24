package cessini.technology.commonui.presentation.globalviewmodels

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.*
import cessini.technology.commonui.utils.networkutil.connectivityStateFlow
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.newapi.model.DataLN
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.appRepository.AppRepository
import cessini.technology.newrepository.authRepository.AuthRepository
import cessini.technology.newrepository.datastores.ProfileStore
import cessini.technology.newrepository.explore.ExploreRepository
import cessini.technology.newrepository.myworld.ProfileRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    application: Application,
    private val authPreferences: AuthPreferences,
    private val profileStore: ProfileStore,
    private val authRepo: AuthRepository,
    private val exploreRepository: ExploreRepository,
    profileRepository: ProfileRepository,
    private val userIdentifierPreferences: UserIdentifierPreferences,
    private val appRepository: AppRepository,
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "BaseViewModel"
    }

    lateinit var authEntity: AuthEntity

    val isInternetAvailable = connectivityStateFlow(application.applicationContext)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            // Setting initial value to `true` assumes that we are connected to the internet until
            // the actual data is available form the flow
            initialValue = true
        )

    private val _authFlag = MutableLiveData(false)
    val authFlag: LiveData<Boolean> get() = _authFlag

    private val _googleAuthFlag = MutableLiveData<Boolean>()
    val googleAuthFlag: LiveData<Boolean> get() = _googleAuthFlag

    val notifications: StateFlow<DataLN?> = flow {
        val res = kotlin.runCatching {
            exploreRepository.getNotifications()
        }
        res.onSuccess {
            emit(it.data)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isBadgeVisible = notifications.map {
        (it?.myWorldNotifications?.size ?: 0) > 0
    }

    private val _id = MutableLiveData("")
    val id: LiveData<String> get() = _id

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _channelName = MutableLiveData("")
    val channelName: LiveData<String> get() = _channelName

    private val _bio = MutableLiveData("")
    val bio: LiveData<String> get() = _bio

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _profileImage = MutableLiveData("")
    val profileImage: LiveData<String> get() = _profileImage

    private val _currentLocation = MutableLiveData("")
    val currentLocation: LiveData<String> get() = _currentLocation

    private val _accessToken = MutableLiveData("")
    private val _refreshToken = MutableLiveData("")

    private val _expertise = MutableLiveData("")
    val expertise: LiveData<String> get() = _expertise

    private val _isPermission = MutableLiveData(false)
    val isPermission: LiveData<Boolean> get() = _isPermission

    private var _deviceId = MutableLiveData("")
    val deviceId: LiveData<String> get() = _deviceId

    lateinit var sharedPref: SharedPreferences

    lateinit var edit: SharedPreferences.Editor

    val profile = profileRepository.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val isLoggedIn by userIdentifierPreferences::loggedIn

    val isDarkThemeEnabled = appRepository
        .isDarkThemeEnabled
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appRepository.setDarkThemeEnabled(enabled)
        }
    }

    val isOnBoardingFinished: StateFlow<Boolean?> = appRepository.isBoardingFinished.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    fun setDeviceId(id: String) {
        _deviceId.value = id
    }

    /*function to check if the use is already signed in or not */
    fun checkUserSignedIn() {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                Log.d(TAG, "Checking if the user is signed in or not")
                authRepo.getAllAuth()
            }

            res.onSuccess {
                Log.d(TAG, "User is signed in, AuthList Size: ${it.size}")
                authEntity = it[0]
                _authFlag.value = true
                _googleAuthFlag.value = true
            }

            res.onFailure {
                Log.d(TAG, "User is not signed in")
                authEntity = AuthEntity("", "", "", "Newbie", "", "", "", "", "", "", "")
                _authFlag.value = false
                _googleAuthFlag.value = false
            }

        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                Log.d(TAG, "Loading User Info")
                authRepo.getAllAuth()
            }

            res.onSuccess {
                Log.d(TAG, "User info loaded, AuthList Size: ${it.size}")
                authEntity = it[0]
                updateUserInfo()

            }

            res.onFailure {
                Log.d(TAG, "User info load failed")
            }

        }
    }

    /*function to update user details*/
    private fun updateUserInfo() {
        _id.value = authEntity.id
        _name.value = authEntity.displayName
        _channelName.value = authEntity.channelName
        _bio.value = authEntity.bio
        _email.value = authEntity.email
        _profileImage.value = authEntity.photoUrl
        _accessToken.value = authEntity.access_token
        _refreshToken.value = authEntity.refresh_token
        _currentLocation.value = authEntity.location
//        expertise.value=authEntity.expertise
        _expertise.value = ""
        Log.d(TAG, "User Info updated on UI: $authEntity")
    }

    private fun resetUserInfo() {
        _id.value = ""
        _name.value = ""
        _channelName.value = ""
        _bio.value = ""
        _email.value = ""
        _profileImage.value = ""
        _accessToken.value = ""
        _refreshToken.value = ""
        _expertise.value = ""
    }

    private fun nukeAuthTable() {
//        val authRepo = AuthRepository(application)
        viewModelScope.launch {
            authRepo.nukeAuthTable()
        }
    }


    fun nukeUserAtSignOut() {
        authPreferences.accessToken = ""
        viewModelScope.launch { profileStore.deleteProfile() }
        /** turing off the flags */
        _authFlag.value = false
        _googleAuthFlag.value = false

        /** need to clear token only after the socket is terminated */
        authRepo.clearRetrofitAuthToken()

        /** resetting the persistent information and UI information */
        nukeAuthTable()
        resetUserInfo()
    }

    // Custom method to add a border around circular bitmap
    fun addBorderToCircularBitmap(
        srcBitmap: Bitmap,
        borderWidth: Int,
        borderColor: Int
    ): Bitmap? {
        // Calculate the circular bitmap width with border
        val dstBitmapWidth = srcBitmap.width + borderWidth * 2
        // Initialize a new Bitmap to make it bordered circular bitmap
        val dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888)
        // Initialize a new Canvas instance
        val canvas = Canvas(dstBitmap)
        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth.toFloat(), borderWidth.toFloat(), null)
        // Initialize a new Paint instance to draw border
        val paint = Paint()
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth.toFloat()
        paint.isAntiAlias = true
        canvas.drawCircle(
            (canvas.width / 2).toFloat(),  // cx
            (canvas.width / 2).toFloat(),  // cy
            (canvas.width / 2 - borderWidth / 2).toFloat(),  // Radius
            paint // Paint
        )

        // Return the bordered circular bitmap
        return dstBitmap
    }

    fun setCurrentLocation(address: String?) {
        _currentLocation.value = address.orEmpty()
    }

    fun setIsPermission(value: Boolean) {
        _isPermission.value = value
    }

    fun setGoogleAuthFlag(flag: Boolean) {
        _googleAuthFlag.value = flag
    }

    fun setAuthFlag(flag: Boolean) {
        _authFlag.value = flag
    }
}
