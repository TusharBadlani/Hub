package cessini.technology.myspace.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cessini.technology.commonui.presentation.common.BaseViewModel
import cessini.technology.commonui.presentation.common.request
import cessini.technology.model.Subcategory
import cessini.technology.myspace.create.CreateRoomSharedViewModel.Event
import cessini.technology.myspace.create.CreateRoomSharedViewModel.Event.Failed
import cessini.technology.myspace.create.CreateRoomSharedViewModel.Event.RoomCreated
import cessini.technology.newrepository.explore.RegistrationRepository
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoomSharedViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val userIdentifierPreferences: UserIdentifierPreferences,
    private val registrationRepository: RegistrationRepository
) : BaseViewModel<Event>() {
    val bottomSheetDraggedState = MutableLiveData<Float>()
    val roomTitle = MutableLiveData<String>()
    val time = MutableLiveData<Long>()
    var data= MutableLiveData<Map<String, List<Subcategory>>>()
    var flag=true

    var categorySet = mutableSetOf<String>()
    var subCategorySet = mutableSetOf<Int>()
    var loggedIn = MutableLiveData<Boolean>()

    private val _requestInProgress = MutableLiveData(false)

    val selectedRoomCategories = mutableSetOf<Int>()

    fun updateDraggedState(state:Float) {
        bottomSheetDraggedState.value = state
    }

    fun isLoggedIn(){
        loggedIn.value = userIdentifierPreferences.loggedIn
    }
    fun createRoom() {
        validate(reason = "Enter Title!", { return }) { roomTitle.value != null && roomTitle.value!!.isNotBlank() && roomTitle.value!!.isNotEmpty()}
        validate(reason = "Choose Time!", { return }) { time.value != null }
        validate(reason = "Choose Topic of Hub!", { return }) { !categorySet.isNullOrEmpty() }
        validate(reason = "Log in to Create Hub", { return }) { userIdentifierPreferences.loggedIn }

        return request(
            _requestInProgress,
            block = {
                roomRepository.createRoom(
                    title = roomTitle.value!!,
                    time = time.value!!,
                    private = false,
                    users = emptyList(),
//                    categories = selectedRoomCategories.toList()
                    categories = categorySet.toList(),
                )
            },
            onSuccess = { RoomCreated(name = it).send() },
            onFailure = { Failed(it.message.orEmpty()).send() },
        )
    }

    private inline fun validate(
        reason: String,
        runIfConditionFalse: () -> Unit,
        condition: () -> Boolean,
    ) {
        if (!condition()) {
            Failed(reason).send()
            runIfConditionFalse()
        }
    }

    fun addData(){
        if(flag) {
            viewModelScope.launch {
                var response = registrationRepository.getVideoCategories()
                data.value = response.data
            }
            flag=false
        }
    }

    sealed class Event {
        class RoomCreated(val name: String) : Event()
        class Failed(val reason: String) : Event()
        class ShowToast(val message: String) : Event()
    }
}
