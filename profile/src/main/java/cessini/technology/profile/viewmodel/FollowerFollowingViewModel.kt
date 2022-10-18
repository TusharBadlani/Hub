package cessini.technology.profile.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.model.User
import cessini.technology.newrepository.myworld.FollowRepository
import cessini.technology.newrepository.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowerFollowingViewModel @Inject constructor(
    private val followRepository: FollowRepository,
) : ViewModel() {

    // TODO: Remove this; viewModels must not reference activities
    lateinit var activity: Activity

    companion object {
        private const val TAG = "FollowerFollowingVM"
    }

    var followingList = MutableLiveData<List<User>>()
    var followerList = MutableLiveData<List<User>>()
    var followerFollowingStatus = MutableLiveData<Int>(0)

    fun loadFollowingList(id: String) {
        viewModelScope.launch {
            val response = followRepository.getFollowing(id)
            response.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        followingList.value = result.data
                    }

                    is Resource.Error -> {
                        Log.d(TAG, "Following List Failure: ${result.message}")
                    }
                }
            }

        }
        viewModelScope.launch {
            val res = kotlin.runCatching {
                followRepository.getFollowers(id)
            }

            res.onSuccess {
                followerList.value = it
            }

            res.onFailure {
                Log.d(TAG, "Followers List Failure: ${it.message}")
            }
        }
    }


}
