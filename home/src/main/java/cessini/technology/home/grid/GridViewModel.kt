package cessini.technology.home.grid


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.model.ViewerX
import cessini.technology.model.search.UserLikes
import cessini.technology.newapi.model.LiveRoomData
import cessini.technology.newapi.model.LiveRoomTimeline
import cessini.technology.newapi.model.RoomTimeline
import cessini.technology.newrepository.timlineRoomRepo.LiveRoomRepository
import cessini.technology.newrepository.timlineRoomRepo.TimelineRepository
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


// Sample Data
@HiltViewModel
class GridViewModel @Inject constructor(
    private val repo: TimelineRepository,
    private val lrepo: LiveRoomRepository
): ViewModel() {
    companion object {
        private const val TAG = "GridViewModel"
    }

    var isNetworkConnected = MutableLiveData<Boolean>(true)
//    private var repo: TimelineRepository = TimelineRepository()
//    private var lrepo: LiveRoomRepository = LiveRoomRepository()
    var liveRoomData = MutableLiveData<LiveRoomData>()
    var liveRoom = MutableLiveData<List<LiveRoomTimeline>>()
    var likedRooms = MutableLiveData<UserLikes>()
    private var roomTimelineLive = MutableLiveData<RoomTimeline>()
    var viewerList = ArrayList<ViewerX>()
    val testList = MutableLiveData<MutableList<ViewerX>>()


    // TODO: Remove this; ViewModels must not reference activities
    lateinit var activity: FragmentActivity
    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(): Boolean{
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        isNetworkConnected.value=capabilities != null && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET)
        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }

    fun getTimeLiveRooms() {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                repo.getTimeLineRooms()
            }


            result.onSuccess {
                Log.e("getTimeLiveRooms", "Success: " + it.raw().message.toString())
                roomTimelineLive.postValue(it.body())
            }

            result.onFailure {
                Log.e("getTimeLiveRooms", "Failure: " + it.message.toString())
            }
        }
    }

    fun getListLiveRooms() {
        if(isNetworkConnected.value==true) {
            viewModelScope.launch {
                val result = kotlin.runCatching {
                    lrepo.getLiveRoom()
                }


                result.onSuccess {
                    Log.e("getTimeLiveRooms", "Success: " + it.raw().message.toString())
                    liveRoom.postValue(it.body())
                }

                result.onFailure {
                    Log.e("getTimeLiveRooms", "Failure: " + it.message.toString())
                }
            }
        }
    }



    fun getRoomViews() {

        if(isNetworkConnected.value==true) {
            viewModelScope.launch {
                Log.d(
                    "roomviews",
                    "inside grid viewmodel: ${repo.getRoomViews().body()?.data?.viewers}"
                )
                val gson = GsonBuilder().create()
//             viewerList=gson.fromJson(repo.getRoomViews().body()?.data?.viewers.toString(),Array<ViewerX>::class.java).toList()
                val res = repo.getRoomViews().body()?.data?.viewers
                res?.forEach {
                    viewerList.add(it)
                }

                Log.d("roomviews", "viewer list is : $viewerList")
                testList.value = viewerList
            }
        }

    }

    fun fetchLikedRooms(id:String){
        viewModelScope.launch {
                var response = repo.getLikes(id)
                Log.e("LikedRooms", "${response}")
                response.body().let {
                    likedRooms.value = it
                }
        }
    }

    fun likeRoom(roomCode: String, id:String){
        viewModelScope.launch {
                kotlin.runCatching { repo.likeRoom(roomCode, id) }
                    .onSuccess { Log.e("LikedRomm", "Successfull") }
                    .onFailure { Log.e("LikedRomm", "UnSuccessfull") }
        }
    }
}


