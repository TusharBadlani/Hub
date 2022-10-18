package cessini.technology.explore.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.cvo.exploremodels.*
import cessini.technology.explore.fragment.ExploreSearchFragment
import cessini.technology.model.Room
import cessini.technology.model.search.*
import cessini.technology.newrepository.websocket.search.SearchWebSocket
import cessini.technology.newrepository.explore.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreSearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    companion object {
        private const val TAG = "ExploreSearchVM"
    }

    lateinit var context: ExploreSearchFragment
    val titles = MutableLiveData<ArrayList<String>>()
    var listWords = ArrayList<SearchResponse>()
    var viewPagerCounter = MutableLiveData<Int>(0)
    var tabPosition = 0
//    lateinit var searchRepository: SearchRepository

    // TODO: Remove this; ViewModels must not reference activity
    lateinit var activity: Activity

    //UserSearchFragment
    val friendsModels = MutableLiveData<MutableList<SearchFriendsModel>>()
    val friendsResponseModels = MutableLiveData<MutableList<SearchFriendsModel>>()

    //MySpace
    val myspaceModels = MutableLiveData<MutableList<SearchMySpaceModel>>()
    val myspaceResponseModels = MutableLiveData<ArrayList<SearchMySpaceModel>>()

    lateinit var contextCreator: Context
    lateinit var contextRoom: Context

    //CreatorSearchFragment
    val creatorModels = MutableLiveData<ArrayList<SearchCreatorApiResponse>>()
    val creatorResponseModels = MutableLiveData<ArrayList<SearchCreatorApiResponse>>()

    private val searchWebSocket = SearchWebSocket {
        viewModelScope.launch(Dispatchers.Main) {
            when (it) {
                is SearchResult.MySpaceResult ->
                {
                    Log.e("SearchMySpace",it.myspace.toString())
                    roomResponseModels.value=ArrayList(it.myspace)
                }

                is SearchResult.CreatorResult -> it.creators.map { it.toCvo() }
                    .let { Log.e("SearchCreator",it.toString())
                        creatorResponseModels.value = ArrayList(it) }

                is SearchResult.VideoResult -> {
                    Log.e("SearchVideo",it.videos.toString())
                    videoResponseModels.value = ArrayList(it.videos)
                }
            }
        }
    }

    //VideoSearchFragment
    lateinit var contextVideo: Context
    val videoModels = MutableLiveData<ArrayList<SearchVideoModel>>()
    val videoResponseModels = MutableLiveData<ArrayList<VideoSearch>>()

    val roomResponseModels = MutableLiveData<ArrayList<Room>>()

    val videoHistory = MutableLiveData<ArrayList<SearchHistoryEntity>>()
    val friendHistory = MutableLiveData<ArrayList<SearchHistoryEntity>>()
    val creatorHistory = MutableLiveData<ArrayList<SearchHistoryEntity>>()
    val roomHistory = MutableLiveData<ArrayList<SearchHistoryEntity>>()

    var creator: ArrayList<SearchHistoryEntity> = arrayListOf()
    var friends: ArrayList<SearchHistoryEntity> = arrayListOf()
    var video: ArrayList<SearchHistoryEntity> = arrayListOf()
    var myspace:ArrayList<SearchHistoryEntity> = arrayListOf()


    init {
        titles.value = arrayListOf("Hub","Video", "Creator")

//        if (!this::searchRepository.isInitialized) {
//            searchRepository = SearchRepository(application)
//        }

        getSearchQueryFromDB()

    }

    //To receive all the creators with query
    fun fetchSearchCreatorQueryAPI(query: String) {
        searchWebSocket.send(query, SearchType.creator)
    }
    fun fetchSearchRoomQueryAPI(query: String) {
        searchWebSocket.send(query, SearchType.myspace)
    }



    /**Video Search Fragment**/
    //To receive all the videos and data from repository
    fun fetchSearchVideosQueryAPI(query: String) {
        searchWebSocket.send(query, SearchType.video)
    }

//    fun setTitle(position: Int): String {
//        return adapter.videosFilterList[position].videoTitle
//    }

    fun insertSearchQueryToDB(searchHistoryEntity: SearchHistoryEntity) {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                searchRepository.insertSearchQueryInDB(searchHistoryEntity)
            }

            result.onSuccess {
                Log.d(TAG, "Query inserted to DB: $it")
                video.clear()
                friends.clear()
                myspace.clear()
                creator.clear()
                getSearchQueryFromDB()
            }

            result.onFailure {
                Log.d(TAG, "DB insert FAILURE ${it.message}")
            }
        }
    }

    fun getSearchQueryFromDB() {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                searchRepository.getAllSearchQueryInDB()
            }

            result.onSuccess {
                Log.d(TAG, "Query fetched from DB: $it")
//                allSearchHistory.value = it as ArrayList<SearchHistoryEntity>?

                it!!.asReversed().forEach { item ->
                    Log.d("ExploreSearchFragment", item.toString())

                    when (item.category) {
                        "Video" -> {
                            video.add(item)
                        }
                        "Friends" -> {
                            friends.add(item)
                        }
                        "Creator" -> {
                            creator.add(item)
                        }
                        "Hub" -> {
                            myspace.add(item)
                        }
                    }
                }

                videoHistory.value = video
                creatorHistory.value = creator
                friendHistory.value = friends
                roomHistory.value=myspace

                Log.d(
                    "ExploreSearchFragment",
                    "Video History from DB: ${videoHistory.value}"
                )
                Log.d(
                    "ExploreSearchFragment",
                    "Friend History from DB: ${friendHistory.value}"
                )
                Log.d(
                    "ExploreSearchFragment",
                    "Creator History from DB: ${creatorHistory.value}"
                )

            }

            result.onFailure {
                Log.d(TAG, "DB fetch FAILURE ${it.message}")
            }
        }
    }
}

private fun Room.toCvo() =SearchMySpaceModel(
    id=id,
    room_code=name,
    schedule = time,
    creator = creator,
    title = title,
    listeners = listeners,
    category = categories,
    private = private,
    live = live
)


fun CreatorSearch.toCvo() = SearchCreatorApiResponse(
    id = id,
    name = name,
    channel_name = channelName,
    profile_picture = profilePicture,
)
