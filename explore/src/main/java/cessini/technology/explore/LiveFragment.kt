package cessini.technology.explore

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import cessini.technology.explore.viewmodel.SearchViewModel
import cessini.technology.home.adapter.RoomPagerAdapter
import cessini.technology.model.ViewerX
import cessini.technology.newapi.model.CreatorListeners
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_live.*

@AndroidEntryPoint
class LiveFragment : Fragment() {
    companion object {
        private const val TAG = "LIVEFRAGMENT"
    }
    val viewModel :SearchViewModel by viewModels()
    var liked: HashMap<String, Boolean> = HashMap<String, Boolean> ()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: LiveFragmentArgs by navArgs()
        viewModel.getRoomViews()
        back_button.setOnClickListener {
            findNavController().navigateUp()
        }
        Log.e(TAG,"Inside activity created")
        val type= args.type
        val type1= args.type1
        var list:ArrayList<ArrayList<CreatorListeners>> = ArrayList()
        var titleList:ArrayList<String> = ArrayList()
        var viewers = mutableListOf<ViewerX>()
        viewModel.fetchLikedRooms()
        if(type.equals("Trending Rooms")){
            viewModel.fetchAllTrendingRooms()
        }
        else
        {
            viewModel.fetchSuggestedRooms()
        }
        viewModel.testList.observe(viewLifecycleOwner
        ) {
            viewers = it
            val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels
            val fragmentManager = parentFragmentManager


            viewModel.allTrendingRooms.observe(viewLifecycleOwner) {
                Log.e(TAG, "InsideTrendingRoom")
                list = arrayListOf()
                it.message.forEach { room ->

                    val smalllist: ArrayList<CreatorListeners> = ArrayList()
                    room.creator.let { creator->
                        smalllist.add(
                            CreatorListeners(
                                isCreator = true,
                                _id = creator.Id,
                                channel_name = creator.channelName,
                                name = creator.name,
                                profile_picture = creator.profilePicture
                            )
                        )
                    }
                    room.allowedUser.forEach { allowedUser->
                        smalllist.add(
                            CreatorListeners(
                                isCreator = false,
                                _id = allowedUser.Id,
                                channel_name = allowedUser.channelName,
                                profile_picture = allowedUser.profilePicture
                            )
                        )
                    }
                    Log.d("Heslo", "Listsmall")
                    Log.d("Heslo", smalllist.toString())
                    list.add(smalllist)
                    titleList.add(room.room)
                    Log.e("RoomListTR", "${list.size}")
                    Log.e("TitleListTR", "${titleList.size}")
                }
            }

            viewModel.suggestedroomresponse.observe(viewLifecycleOwner
            ) { it ->
                Log.e(TAG, "InsideSuggestedRoom")
                if (type == "Suggestion Rooms")
                    list = ArrayList()
                val currroom = it.find { suggestionCategoryRoom->
                    suggestionCategoryRoom.categorytitle == type1
                }
                currroom?.rooms?.forEach { roomInfo->
                    liked[roomInfo.roomCode!!] = false
                    val smalllist: ArrayList<CreatorListeners> = ArrayList()
                    var i = 0
                    roomInfo.datas.forEach { roomUsers->
                        smalllist.add(
                            CreatorListeners(
                                isCreator = i == 0,
                                _id = roomUsers.id,
                                channel_name = roomUsers.channelName,
                                name = roomUsers.name,
                                profile_picture = roomUsers.url
                            )
                        )
                        i++

                    }
                    if(!list.contains(smalllist))
                        list.add(smalllist)
                    if(!titleList.contains(roomInfo.roomCode!!))
                        titleList.add(roomInfo.roomCode!!)
                    Log.e("RoomListSR", "${list.size}")
                    Log.e("TitleListSR", "${titleList.size}")
                }
            }
            viewModel.likedRooms.observe(viewLifecycleOwner){ userLikes->
                userLikes.message.forEach { message->
                    message.roomCode?.let { it1 -> liked[it1] = true }
                }
            }

            Log.e("LikedMap","${liked}")
            Log.e("RoomList", "${list.size}")
            Log.e("TitleList", "${titleList.size}")
            val adapter= RoomPagerAdapter(list,titleList,liked,requireContext(),requireActivity(),this,fragmentManager,screenWidth,screenHeight,viewers,2)
            viewPager.orientation= ViewPager2.ORIENTATION_VERTICAL
            viewPager.adapter= adapter
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.e(TAG,"Inside  create View")
        return inflater.inflate(R.layout.fragment_live, container, false)
    }
}