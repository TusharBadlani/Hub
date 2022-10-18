package cessini.technology.myspace.live

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cessini.technology.commonui.common.BaseViewModel
import cessini.technology.model.Room
import cessini.technology.myspace.access.MySpaceFragmentViewModel
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveMySpaceViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val userIdentifierPreferences: UserIdentifierPreferences
) : BaseViewModel<LiveMySpaceViewModel.Event>() {
    companion object {
        private const val TAG = "LiveMySpaceViewModel"
    }

    val isMicOn = MutableStateFlow(false)
    val isVideoOn = MutableStateFlow(true)

    private val _room = MutableStateFlow(Room())
    val room = _room.asStateFlow()

    private val _upiData = MutableStateFlow("")
    val upiData = _upiData.asStateFlow()


    fun getRoomDetails(roomCode: String) {
        viewModelScope.launch {
            runCatching { roomRepository.getRoom(roomCode) }
                .onSuccess { _room.value = it }
                .onFailure { Event.ShowToast(it.message.orEmpty()).send() }
        }
    }

    fun joinRoom(roomName: String) {
        viewModelScope.launch {
            runCatching { roomRepository.joinRoom(roomName) }
                .onSuccess { Event.ShowToast("Join request sent.").send() }
                .onFailure { Event.ShowToast(it.message.orEmpty()).send() }
        }
    }

    fun getCreatorUPIData(creatorId: String) {
        viewModelScope.launch {
            kotlin.runCatching { roomRepository.getCreatorUPIData(creatorId) }
                .onSuccess {
                    _upiData.value = "${it.upiId}^${it.upiName}"
                }
                .onFailure {
                    Log.d(TAG, it.stackTraceToString())
                }
        }
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
    }

}