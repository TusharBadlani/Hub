package cessini.technology.profile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cessini.technology.commonui.common.BaseViewModel
import cessini.technology.newrepository.datastores.ProfileStore
import cessini.technology.newrepository.myworld.ProfileRepository
import cessini.technology.profile.viewmodel.EditUserProfileViewModel.Event
import cessini.technology.profile.viewmodel.EditUserProfileViewModel.Event.ProfileUpdated
import cessini.technology.profile.viewmodel.EditUserProfileViewModel.Event.ShowToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
    private val profileStore: ProfileStore,
    private val profileRepository: ProfileRepository,
) : BaseViewModel<Event>() {
    companion object {
        private const val TAG = "EditProfileViewModel"
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
        object ProfileUpdated : Event()
    }

    val profileChannelName = MutableLiveData("")
    var currentPictureUri = MutableLiveData<String>()

    val profile = profileRepository.profile

    fun updateProfile(
        name: String,
        bio: String,
        channelName: String,
        location: String,
        email: String,
        expertise: String
    ) {
        viewModelScope.launch {
            runCatching {
                profileRepository.updateProfile(
                    name = name,
                    bio = bio,
                    channelName = channelName,
                    location = location,
                    email = email,
                    expertise = expertise,
                    profilePicture = currentPictureUri.value.orEmpty(),
                )
            }.onFailure { ShowToast(it.message.orEmpty()).send() }
                .onSuccess {
                    try {
                        profileStore.storeProfile(profileRepository.getProfile())
                        ProfileUpdated.send()
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
        }
    }

    var username = MutableLiveData<String>()

    var bio = MutableLiveData("")
    var channelName = MutableLiveData("")
    var profilePicture = MutableLiveData("")

    var profileBitmap = MutableLiveData<Bitmap>()

    var location = MutableLiveData("")


    var oldFileUri = MutableLiveData<String>()

    fun checkChannelNameIsAvailable(name: String) {
        viewModelScope.launch {
            runCatching {
                profileRepository.channelNameAvailable(name)
            }.onSuccess {
                ShowToast("Channel name is available").send()
            }.onFailure {
                ShowToast(it.message.orEmpty()).send()
            }
        }
    }

    var validFlag = MutableLiveData(true)

    /** Returning a Unique TimeStamp for every time .
     * Each timeStamp will be the current time in the system .
     */
    fun getTimeStamp(): String {
        /** Creating a Unique TimeStamp for every image captured .
         * Each timeStamp will be the current time in the system .
         */
        return SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    }

    /**Function to convert bitmap to byte array.*/
    fun saveImage(TAG: String, bitmap: Bitmap, fileName: String, context: Context): String {

        /** Setting up the root path for the directory.*/
        val root = context.getExternalFilesDir("/Pictures")

        /** Creating my own Directory in Storage.*/
        val myDirectory = File("$root")
        myDirectory.mkdirs()

        /**Initializing a new file.*/
        val file = File(myDirectory, fileName)

        /**If the File with the same Name is Present ,
         * Then , Delete the old file.
         */
        deleteOldFile(oldFileUri.value)

        /** Creating a new file.*/
        file.createNewFile()

        oldFileUri.value = file.absolutePath

        Log.d(TAG, "File created at : ${file.absolutePath}")

        try {
            Log.d(TAG, "FileOutputStream has started.")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            Log.d(TAG, "FileOutputStream has ended.")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    fun deleteOldFile(uri: String?) {
        /**If the File with the same Name is Present ,
         * Then , Delete the old file.
         */
        if (!uri.isNullOrEmpty()) {
            val file = File(uri)
            Log.d(TAG, "Old File at : $file")
            if (file.exists()) {
                Log.d(TAG, "${file.name} : $file exists.")
                if (file.delete()) {
                    Log.d(TAG, "Old File at $file deleted successfully.")
                } else {
                    Log.d(TAG, "Old File at $file failed to delete.")
                }
            } else {
                Log.d(TAG, "File : $file does not exists.")
            }
        }
    }

    /** Function to delete the old files that are not requires any further. */
    fun deleteDirectory(context: Context) {
        val directory = File("${context.getExternalFilesDir("/Pictures")}")
        if (directory.isDirectory) {
            val innerFiles = directory.list()
            if (innerFiles.size > 5) {
                for (i in innerFiles.indices) {
//                    Log.d(TAG , "${File(directory, innerFiles[i]).name} Deleted Successfully.")
                    File(directory, innerFiles[i]).delete()
                }
            }
        }
    }

    fun resetEditProfileData() {
        bio.value = ""
        channelName.value = ""
        profilePicture.value = ""
        validFlag.value = true
    }
}