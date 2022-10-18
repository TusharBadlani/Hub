package cessini.technology.commonui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.model.Comment
import cessini.technology.newrepository.video.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {
    companion object {
        private const val TAG = "CommentsVM"
    }

    private val _currVideoId = MutableLiveData("")
    val currVideoId: LiveData<String> get() = _currVideoId

    private val _commentsList = MutableLiveData<ArrayList<Comment>>()
    val commentsList: LiveData<ArrayList<Comment>> get() = _commentsList

    fun getCommentsList(videoId: String) {
        viewModelScope.launch {
            val res = runCatching {
                videoRepository.getComments(videoId)
//                homeFeedRepository.fetchCommentsList(videoId!!)
            }

            res.onSuccess {
                _commentsList.value = ArrayList(it)
                Log.d(TAG, "Comment List received ${commentsList.value}")
            }

            res.onFailure {
                Log.d(TAG, "Comment List fetch failed: ${it.message}")
            }
        }

    }

    fun postCommentVideo(comment: String) {
        viewModelScope.launch {
            val res = runCatching {
                videoRepository.comment(currVideoId.value.orEmpty(), comment)
//                homeFeedRepository.postComment(currVideoId.value!!, comment)
            }

            res.onSuccess {
//                Log.d(TAG, it.message)
            }

            res.onFailure {
                Log.d(TAG, it.message.toString())
            }
            getCommentsList(currVideoId.value.orEmpty())
        }

    }

    fun setCurrVideoId(id: String) {
        viewModelScope.launch {
            _currVideoId.value = id
        }
    }

}
