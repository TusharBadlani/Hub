package cessini.technology.myspace.access

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cessini.technology.commonui.common.BaseViewModel
import cessini.technology.model.Room
import cessini.technology.myspace.access.MySpaceFragmentViewModel.Event
import cessini.technology.myspace.access.MySpaceFragmentViewModel.Event.ShowToast
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySpaceFragmentViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
) : BaseViewModel<Event>() {

    val isMicOn = MutableStateFlow(true)
    val isVideoOn = MutableStateFlow(true)

    private val _room = MutableStateFlow(Room())
    val room = _room.asStateFlow()

    val mySpaceFragmentState = MutableLiveData<Int>()

    fun updateRoom(roomName: String) {
        viewModelScope.launch {
            runCatching { roomRepository.getRoom(roomName) }
                .onSuccess {
                    var temp=it
                    if(!temp.listeners.isNullOrEmpty())
                        temp.listeners = temp.listeners.toSet().toList()
                    _room.value = temp
                Log.e("MySpaceFragmentVM","${temp}")}
                .onFailure { ShowToast(it.message.orEmpty()).send() }
        }
    }

    fun joinRoom(roomName: String) {
        viewModelScope.launch {
            runCatching { roomRepository.joinRoom(roomName) }
                .onSuccess { ShowToast("Join request sent.").send() }
                .onFailure { ShowToast(it.message.orEmpty()).send() }
        }
    }

    fun deleteRoom(roomName: String) {
        viewModelScope.launch {
            runCatching { roomRepository.deleteRoom(roomName) }
                .onSuccess { ShowToast("Room deleted successfully.").send() }
                .onFailure { ShowToast(it.message.orEmpty()).send() }
        }
    }

    fun removeListener(roomName: String, profileID: String){
        viewModelScope.launch {
            runCatching{ roomRepository.removeListener(roomName, profileID)}
                .onSuccess { ShowToast("Room Left Successfully").send() }
                .onFailure { ShowToast(it.message.orEmpty()).send() }
        }
    }

    fun likeRoom(roomCode: String,id:String){
        viewModelScope.launch {
            kotlin.runCatching { roomRepository.likeRoom(roomCode,id) }
                .onSuccess { Log.e("LikedRomm","Successfull") }
                .onFailure { Log.e("LikedRomm","UnSuccessfull") }


        }
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
    }
}
