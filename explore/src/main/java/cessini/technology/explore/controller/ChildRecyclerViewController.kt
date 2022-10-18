package cessini.technology.explore.controller

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.cvo.exploremodels.CategoryModel
import cessini.technology.cvo.exploremodels.ProfileModel
import cessini.technology.explore.R
import cessini.technology.explore.childItem2
import cessini.technology.explore.childItemRoom
import cessini.technology.explore.epoxy.voice
import cessini.technology.explore.states.ExploreOnClickEvents
import cessini.technology.explore.upcomingMyspace
import cessini.technology.explore.viewmodel.SearchViewModel
import cessini.technology.model.*
import cessini.technology.navigation.MainNavGraphDirections
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.websocket.video.RoomViewerUpdaterWebSocket
import com.airbnb.epoxy.AsyncEpoxyController
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChildRecyclerViewController(
    var context: Context,
    var parentIndex: Int,
    var viewModel: SearchViewModel,
    var activity: FragmentActivity,
    val fragment: Fragment,
    val roomRepository: RoomRepository,
    val userIdentifierPreferences: UserIdentifierPreferences,
    var onClickListener: (event: ExploreOnClickEvents) -> Unit
) : AsyncEpoxyController() {

    var followingStatusID: HashMap<String, Boolean> = HashMap<String, Boolean>()
    private val galleryViewModel by fragment.activityViewModels<GalleryViewModel>()

    @Inject
    lateinit var authPreferences: AuthPreferences

    //    val authPreferences = AuthPreferences(context)
    @Inject
    lateinit var roomViewerUpdaterWebSocket: RoomViewerUpdaterWebSocket
    var find = true


    var categoryVideoList: MutableList<Video> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }
    var category: CategoryModel = CategoryModel(0, "")

    var topProfiles: MutableList<TopProfile> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var rooms: MutableList<Room> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var liveRooms: MutableList<LiveRoom> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var trendingRooms: MutableList<List<MessageT>> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        when (parentIndex) {
            1 -> {
                setupVoicesToFollow()
            }
            2 -> {
                /*TODO: setupLiveRoom()*/
                Unit
            }
            5 -> {
                setupUpcomingHub()
            }
//            4 -> {
//                liveRooms.forEach { room ->
//                    childItemRoom {
//                        (room.listeners as List<Listener>).let {
//                            id(room._id)
//                            roomTitle(room.title)
//
//                            val moreThan3Listener = if (it.size < 4) {
//                                "0"
//                            } else {
//                                (5 - it.size).toString()
//                            }
//
//                            listenerCount(moreThan3Listener)
////                                            onClick(View.OnClickListener {
////                                                (activity as ToFlowNavigable).navigateToFlow(
////                                                    NavigationFlow.AccessRoomFlow(room.title!!)
////                                                )
////                                            })
//                            listener1Image(room.creator!!.profile_picture)
//                            listener2Image(it.getOrNull(0)?.profile_picture ?: "")
//                            listener3Image(it.getOrNull(1)?.profile_picture ?: "")
//                            listener4Image(it.getOrNull(2)?.profile_picture ?: "")
//                            listener5Image(it.getOrNull(3)?.profile_picture ?: "")
//                            searchViewModel(viewModel)
//                            fragment(fragment)
//
//                            // Touch effect
//                            val touchTimeFactor = 200
//                            var touchDownTime = 0L
//
//                            onTouchDetected { view, event ->
//
//                                if (event.action == MotionEvent.ACTION_DOWN) {
//                                    view.animate().scaleX(0.98f).scaleY(0.98f).duration = 20
//                                    Log.i("ChildRecyclerViewRoom", "Pressed")
//                                    touchDownTime = System.currentTimeMillis()
//
//                                } else if (event.action == MotionEvent.ACTION_UP) {
//                                    view.animate().scaleX(1f).scaleY(1f).duration = 20
//                                    Log.i("ChildRecyclerViewRoom", "Released")
//                                    val isClickTime =
//                                        System.currentTimeMillis() - touchDownTime < touchTimeFactor
//                                    if (isClickTime) {
//                                        view.performClick()
//                                        Log.i("ChildRecyclerViewRoom", "Clicked")
//                                        val intent = Intent(activity, LiveMyspaceActivity::class.java)
//                                        intent.putExtra("ROOM_CODE",room.room_code)
//                                        activity.startActivity(intent)
//                                        activity.overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation)
////                                        (activity as ToFlowNavigable).navigateToFlow(
////                                            NavigationFlow.AccessRoomFlow(room.room_code!!)
////                                        )
//                                    }
//
//                                }
//                                false
//                            }
//
//                        }
//
//                    }
//                }
//            }
            else -> {
                categoryVideoList.forEachIndexed { index, categoryVideo ->
                    childItem2 {
                        id(index)
                        positionType(categoryVideo.positionType)
                        childItem2(categoryVideo)
                        onClickChild2 { _, _, _, position ->
                            val galleryViewModel =
                                activity.run {
                                    ViewModelProvider(this)[GalleryViewModel::class.java]
                                } ?: throw Exception("Invalid Activity")

                            Log.i("ChildRecycler", "Entering")

                            galleryViewModel.setVideoPos(position)
                            galleryViewModel.setVideoFlow(1)
                            galleryViewModel.setVideoDisplayList(ArrayList(categoryVideoList))

                            galleryViewModel.setCommonVideoProfile(
                                ProfileModel(
                                    id = categoryVideo.profile.id,
                                    channel_name = categoryVideo.profile.channel,
                                    name = categoryVideo.profile.name,
                                    profile_picture = categoryVideo.profile.picture,
                                )
                            )

                            fragment.findNavController().navigate(
                                MainNavGraphDirections.actionGlobalGlobalVideoDisplayFlow()
                            )
                        }
                    }
                }
            }
        }

    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    interface ChildRecyclerViewRippleListener {
        fun onRipple()
    }

    fun addFollowingStatus() {
        viewModel.following.observe(activity) { it ->
            it?.forEachIndexed { index, user ->
                followingStatusID[user.id] = true
            }
            requestModelBuild()
        }
    }

    private fun setupLiveRoom() {
        trendingRooms.forEach { room ->
            childItemRoom {
                id(room[0].room)
                roomTitle(room.get(0).title)
                val moreThan3Listener = if (room[0].allowedUser.size < 4) {
                    "0"
                } else {
                    (5 - room[0].allowedUser.size).toString()
                }
                listenerCount(moreThan3Listener)

                listener1Image(room[0].creator.profilePicture)
                listener2Image(room[0].allowedUser.getOrNull(0)?.profilePicture ?: "")
                listener3Image(room[0].allowedUser.getOrNull(1)?.profilePicture ?: "")
                listener4Image(room[0].allowedUser.getOrNull(2)?.profilePicture ?: "")
                listener5Image(room[0].allowedUser.getOrNull(3)?.profilePicture ?: "")
                searchViewModel(viewModel)
                fragment(fragment)

                // Touch effect
                val touchTimeFactor = 200
                var touchDownTime = 0L

                onTouchDetected { view, event ->

                    if (event.action == MotionEvent.ACTION_DOWN) {
                        view.animate().scaleX(0.98f).scaleY(0.98f).duration = 20
                        Log.i("ChildRecyclerViewRoom", "Pressed")
                        touchDownTime = System.currentTimeMillis()

                    } else if (event.action == MotionEvent.ACTION_UP) {
                        view.animate().scaleX(1f).scaleY(1f).duration = 20
                        Log.i("ChildRecyclerViewRoom", "Released")
                        val isClickTime =
                            System.currentTimeMillis() - touchDownTime < touchTimeFactor
                        if (isClickTime) {
                            view.performClick()
                            Log.i("ChildRecyclerViewRoom", "Clicked")
//                                    val intent = Intent(activity, LiveMyspaceActivity::class.java)
//                                    intent.putExtra("ROOM_CODE",room.get(0).roomCode)
//                                    activity.startActivity(intent)
//                                    activity.overridePendingTransition(R.anim.slide_out_animation, R.anim.slide_in_animation)
////                                        (activity as ToFlowNavigable).navigateToFlow(
//                                            NavigationFlow.AccessRoomFlow(room.room_code!!)
//                                        )

                            onClickListener(
                                ExploreOnClickEvents.ExploreFragmentToLiveFragment(
                                    "Trending Rooms",
                                    "abc"
                                )
                            )

                        }

                    }
                    false
                }


            }

        }
    }

    private fun setupUpcomingHub() {

        rooms.forEach {
            if (it.listeners.isNotEmpty()) {
                it.listeners = it.listeners.toSet().toList()
            }
        }
        rooms.forEach { room ->
            //code for removing all myspaces
//                    if(!room.time.compareDate(System.currentTimeMillis()))
//                        return@forEach
            var text = ""
            var categorytext = ""
            authPreferences = AuthPreferences(context)
            roomViewerUpdaterWebSocket =
                RoomViewerUpdaterWebSocket(authPreferences, userIdentifierPreferences)

            Log.d(
                "RoomViewerSocket",
                "inside room pager adapter:  ${
                    roomViewerUpdaterWebSocket.invoke(
                        room.id,
                        1
                    )
                }"
            )
            upcomingMyspace {
                id(room.id)
                creatorName(room.creator.name)
                time(room.time)

                if (room.categories.isNotEmpty()) {
                    val category = StringBuilder()
                    room.categories.forEach {
                        category.append(categories[it.toInt()] + "#")
                    }
                    val fh = category.indexOf("#")
                    val sh = category.indexOf("#", fh + 1)
                    val th = category.indexOf("#", sh + 1)
                    categorytext += "#" + category.substring(1, fh) + " "
                    if (sh != -1) {
                        categorytext += "#" + category.substring(fh + 2, sh) + " "

                    } else

                        if (th != -1) {
                            categorytext += "#" + category.substring(sh + 2, th) + " "
                        }

                }

                text = context.getString(R.string.some_text, room.title, categorytext)

                span(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
                listener1Image(room.creator.profilePicture)
                listener2Image(room.listeners.getOrNull(0)?.profile_picture ?: "")
                listener3Image(room.listeners.getOrNull(1)?.profile_picture ?: "")

                onClick { _ ->
                    Log.e("RoomName", room.name)

                    onClickListener(ExploreOnClickEvents.ToAccessRoomFlow(room))

                }
                onJoin { view ->
                    if (!userIdentifierPreferences.loggedIn) {
                        (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.AuthFlow)

                    } else {
                        activity.lifecycleScope.launch {
                            runCatching { roomRepository.joinRoom(room.name) }
                                .onSuccess {
                                    Toast.makeText(
                                        activity,
                                        "Join request sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    view.setBackgroundResource(R.drawable.round_enable_viewbutton)
                                }
                        }
                    }
                }
            }
        }
    }

    private fun setupVoicesToFollow() {
        topProfiles.forEachIndexed { index, topProfile ->
            followingStatusID.put(topProfile.id, false)
        }

        topProfiles.forEachIndexed { index, topProfilesModel ->
            if (topProfilesModel.id != (activity as HomeActivity).baseViewModel.id.value) {
                voice {
                    id(topProfilesModel.id)
                    img(topProfilesModel.profilePicture)
                    title1(topProfilesModel.name)
                    vid(topProfilesModel.id)
                    channelName(topProfilesModel.channelName)
                    activity(activity)
                    followingStatusId(followingStatusID)
                    onClickEvents(onClickListener)
                }
            }
        }
    }


}

val categories = listOf(
    " Film and animation ",
    " Cars and vehicles ",
    " Music ",
    " Pets and animals ",
    " Sport ",
    " Travel and events ",
    " Gaming ",
    " People and blogs ",
    " Comedy ",
    " Entertainment ",
    " News and politics ",
    " How-to and style ",
    " Education ",
    " Science and technology ",
    " Non-profits and activism ",
)