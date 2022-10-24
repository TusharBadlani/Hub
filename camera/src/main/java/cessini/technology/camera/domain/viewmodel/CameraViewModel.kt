package cessini.technology.camera.domain.viewmodel

import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.camera.presentation.fragment.CameraFragment
import cessini.technology.commonui.presentation.HomeActivity
import cessini.technology.model.Audio
import cessini.technology.model.SongInfo
import cessini.technology.newrepository.explore.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "CameraViewModel"
    }

    var listOfAudios = MutableLiveData<ArrayList<Audio>>()
    lateinit var cameraFragment: CameraFragment
    var audio = MutableLiveData<SongInfo>()

    var isService = MutableLiveData<Boolean>(false)

    /**Get Audio From the Repository.*/
    fun loadAudio(root: Context) {
        /**Checking if the audioRepository is initialized , If not initialized then we initialize it.*/

        /**Launching a coroutine for getting the audios from the repository.*/
        viewModelScope.launch {
            val categoryResult = kotlin.runCatching {
                songsRepository.getSongs()
            }

            /**Storing the fetched list of audio in the variable listOfAudios.*/
            categoryResult.onSuccess {
                Log.d(TAG, "Audio Load Successful: $it")
                listOfAudios.value = it
            }
            categoryResult.onFailure {
                Log.d(TAG, "Audio Load Failed: ${it.message}")
            }
        }
    }

    /** Function to Set the State of the microphone either true -> MUTED or false ->UN MUTED .*/
    fun setMicMuted(activity: HomeActivity, state: Boolean = false): Boolean {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isMicrophoneMute = state
        return audioManager.isMicrophoneMute
    }
}