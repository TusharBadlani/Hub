package cessini.technology.home.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cessini.technology.commonui.common.BaseViewModel
import cessini.technology.model.Video
import cessini.technology.home.viewmodel.SocketFeedViewModel.Event
import cessini.technology.home.webSockets.HomeFeedWebSocket
import cessini.technology.home.webSockets.model.HomeFeedSocketPayload
import cessini.technology.home.webSockets.model.HomeFeedSocketResponse
import cessini.technology.model.search.SearchResult
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.utils.Resource
import cessini.technology.newrepository.websocket.search.SearchWebSocket
import cessini.technology.newrepository.websocket.suggestion.SuggestionWebSocket
import cessini.technology.newrepository.websocket.timeline.TimelineWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketFeedViewModel @Inject constructor(
    userIdentifierPreferences: UserIdentifierPreferences,
) : BaseViewModel<Event>() {

    private val _videos = MutableStateFlow(emptyList<Video>())
    val videos = _videos.asStateFlow()

    val homeFeeds = MutableLiveData<HomeFeedSocketResponse>()

    private val deviceId = userIdentifierPreferences.uuid

    private val _suggestedVideos = MutableStateFlow(emptyList<Video>())
    val suggestedVideos = _suggestedVideos.asStateFlow()

    private val timelineSocketBlock: (List<Video>) -> Unit = {
        _videos.value += it
    }

    private var timelineWebSocket = TimelineWebSocket(deviceId, timelineSocketBlock)

    private val suggestionWebSocket = SuggestionWebSocket { _suggestedVideos.value = it }

    private val homeFeedWebSocket = HomeFeedWebSocket {
        viewModelScope.launch(Dispatchers.Main) {
            homeFeeds.value = it
        }
    }

    fun sendUserPayload(payload: HomeFeedSocketPayload) {
        homeFeedWebSocket.send(payload)
    }

    fun fetchMoreVideos() {
        timelineWebSocket.next()
    }

    fun suggest(videoId: String) {
        suggestionWebSocket.suggest(videoId)
    }

    fun suggestedVideoClicked(position: Int) {
        Event.SuggestionClicked.send()

        viewModelScope.launch {
            delay(100)
            with(_suggestedVideos.value) {
                _videos.value = subList(position, size)
            }
        }
    }

    fun resetTimeline() {
        timelineWebSocket.close()
        _videos.value = emptyList()

        timelineWebSocket = TimelineWebSocket(deviceId, timelineSocketBlock)
    }

    override fun onCleared() {
        super.onCleared()

        timelineWebSocket.close()
        suggestionWebSocket.close()
    }

    sealed class Event {
        object SuggestionClicked : Event()
    }
}
