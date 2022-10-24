package cessini.technology.home.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.PagerSnapHelper
import cessini.technology.commonui.presentation.HomeActivity
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.presentation.common.isInDarkTheme
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.commonui.presentation.globalviewmodels.GalleryViewModel
import cessini.technology.home.R
import cessini.technology.home.controller.HomeEpoxyController
import cessini.technology.home.databinding.NewHomeFragmentBinding
import cessini.technology.home.model.HomeEpoxyStreamsModel
import cessini.technology.home.model.JoinRoomSocketEventPayload
import cessini.technology.home.model.User
import cessini.technology.home.viewmodel.HomeFeedViewModel
import cessini.technology.home.viewmodel.SocketFeedViewModel
import cessini.technology.home.webSockets.HomeSignallingClient
import cessini.technology.home.webSockets.SocketEventCallback
import cessini.technology.home.webSockets.model.HomeFeedSocketPayload
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.myworld.ProfileRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.video.VideoRepository
import cessini.technology.newrepository.websocket.video.VideoViewUpdaterWebSocket
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment<NewHomeFragmentBinding>(R.layout.new_home_fragment),
    LifecycleObserver,
    View.OnTouchListener {

    lateinit var fragmentStories: StoriesFragment

    @Inject
    lateinit var viewsUpdater: VideoViewUpdaterWebSocket

    @Inject
    lateinit var profileRepository: ProfileRepository

//    lateinit var blurLayout: BlurLayout

    private val galleryViewModel by activityViewModels<GalleryViewModel>()

//    lateinit var blurLayout: BlurLayout

    companion object {
        private const val TAG = "HomeFragment"

    }

    @Inject
    lateinit var videoRepository: VideoRepository
    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences
    @Inject
    lateinit var authPreferences: AuthPreferences

    private val homeFeedViewModel: HomeFeedViewModel by viewModels()
    private val socketFeedViewModel: SocketFeedViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CREATED")
        authPreferences = AuthPreferences(requireContext())
        userIdentifierPreferences = UserIdentifierPreferences(requireContext(), authPreferences)

        Log.d("CheckNav", "HomeFragment, ${activity?.localClassName}")

        /*homeFeedViewModel.activity = requireActivity()*/
        /**Setting up the viewModel to the Binding.*/
        binding.homeViewModel = homeFeedViewModel //attach your viewModel to xml
        /**Setting a lifecycleOwner as this Fragment.*/
        binding.lifecycleOwner = viewLifecycleOwner

        homeFeedViewModel.isUserSignedIn()
        homeFeedViewModel.authFlag.observe(viewLifecycleOwner, Observer { isSignedIn->
            if (!isSignedIn) {
                (requireActivity() as ToFlowNavigable).navigateToFlow(
                    NavigationFlow.AuthFlow
                )
            } else {
                homeFeedViewModel.loadUserInfo()
            }
        })


        //blurview
        // blurLayout = binding.blurLayout


        /*if (!isInDarkTheme()) {
            (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.camera_flow)
                .setIcon(R.drawable.ic_camera_white)
            (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.explore_flow)
                .setIcon(R.drawable.ic_search_white)
        }
        if ((activity as HomeActivity).profileDrawable != null) {
            (activity as HomeActivity).setUpNavProfileIcon(
                null, (activity as HomeActivity).profileDrawable, false
            )
        } else {
            if (!isInDarkTheme()) {
                (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.profile_flow)
                    .setIcon(R.drawable.ic_profile_white)
            }
        }*/

//        setUpNavViewColor("#121214")

        /*fragmentVideoSuggestion =
            childFragmentManager.findFragmentByTag("frag_video_suggestion") as VideoSuggestionFragment

        fragmentStories =
            childFragmentManager.findFragmentByTag("frag_stories") as StoriesFragment

        fragmentVideoSuggestion.view?.visibility = View.INVISIBLE
        fragmentStories.view?.visibility = View.VISIBLE

        binding.viewpager.offscreenPageLimit = 2*/

        /*ExoProvider.get(requireActivity())
            .register(this, memoryMode = MemoryMode.HIGH)
            .addBucket()*/

        /*showShimmer()

        setUpHomeFeed()*/

        // TODO: To be replaced with Pagination
        socketFeedViewModel.sendUserPayload(HomeFeedSocketPayload(
            user_id = userIdentifierPreferences.uuid,
            page = 1
        ))



        val controller = HomeEpoxyController(requireContext()) { joinRoomSocketEventPayload ->
            joinRoomRequest(convertToJSONObject(joinRoomSocketEventPayload))
        }
        val snapHelper = PagerSnapHelper()
        /*TODO:
           lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData: PagingData<home_data> ->
                controller.submitData(pagingData)
            }
        }*/

        binding.recyclerView.setController(controller)
        snapHelper.attachToRecyclerView(binding.recyclerView)

        val DUMMY_HLS_FEED_LINK = "http://amssamples.streaming.mediaservices.windows.net/69fbaeba-8e92-4740-aedc-ce09ae945073/AzurePromo.ism/manifest(format=m3u8-aapl)"
        val DUMMY_DATA = HomeEpoxyStreamsModel(
            link = DUMMY_HLS_FEED_LINK,
            title = "Nothing to display in feeds!",
            room = "",
            admin = "MyWorld",
            user = User(
                channelName = "",
                email = "",
                id = "",
                name = "",
                profilePicture = ""
            ),
            email = ""
        )


        socketFeedViewModel.homeFeeds.observe(viewLifecycleOwner, Observer { homeFeedSocketResponse->
            val homeEpoxyStreams = mutableListOf<HomeEpoxyStreamsModel>()
            homeFeedSocketResponse.data.map { dataResponse ->
                homeEpoxyStreams.add(
                    HomeEpoxyStreamsModel(
                        link = DUMMY_HLS_FEED_LINK,
                        title = dataResponse.title,
                        room = dataResponse.room,
                        admin = dataResponse.admin,
                        user = User(
                            channelName = homeFeedViewModel.channelName.toString(),
                            email = homeFeedViewModel.email.toString(),
                            id = homeFeedViewModel.userId.toString(),
                            name = homeFeedViewModel.username.toString(),
                            profilePicture = homeFeedViewModel.profilePicture.toString()
                        ),
                        email = homeFeedViewModel.email.toString()
                    )
                )
            }
            homeFeedViewModel.homeEpoxyStreamsList.value = homeEpoxyStreams
        })
        if(!homeFeedViewModel.homeEpoxyStreamsList.value.isNullOrEmpty()) {
            controller.streams = homeFeedViewModel.homeEpoxyStreamsList.value!!
        } else {
            controller.streams = mutableListOf(DUMMY_DATA)
        }

        systemBarInsetsEnabled = false

//        setUpNavIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_homeactive))
    }


    private fun convertToJSONObject(joinRoomSocketEventPayload: JoinRoomSocketEventPayload):JSONObject {
        val jsonObject = JSONObject()
        val userJSONObject = JSONObject()
        try {
            jsonObject.put("room", joinRoomSocketEventPayload.room)
            userJSONObject.put("id", joinRoomSocketEventPayload.user.id)
            userJSONObject.put("name", joinRoomSocketEventPayload.user.name)
            userJSONObject.put("profilePicture", joinRoomSocketEventPayload.user.profilePicture)
            userJSONObject.put("email", joinRoomSocketEventPayload.user.email)
            userJSONObject.put("channelName", joinRoomSocketEventPayload.user.channelName)
            jsonObject.put("user", userJSONObject)
            jsonObject.put("email", joinRoomSocketEventPayload.user.email)
        } catch (e:JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun joinRoomRequest(data:JSONObject) {
        val homeSignallingClient = HomeSignallingClient()
        Toast.makeText(context, "Join Room Request Sent", Toast.LENGTH_LONG).show()

        homeSignallingClient.requestJoinRoom(object : SocketEventCallback {
            override fun onJoinRequestAccepted(socketId: String) {
                Toast.makeText(context, "Room Joined: $socketId", Toast.LENGTH_LONG).show()
            }

            override fun onJoinRequestDenied(msg: String) {
                Toast.makeText(context, "Join Request Denied: $msg", Toast.LENGTH_LONG).show()
            }
        }, data)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        activity?.run {
            galleryViewModel.setStoryPos(0)
        }

        ProfileConstants.story = false


        /** Loading data*/
        //binding.progrssBarNoVideo.visibility = View.VISIBLE

        homeFeedViewModel.loadHomeFeed()
    }

    /**Function to setup the HOME FEED into the UI Window.*/
    @RequiresApi(Build.VERSION_CODES.M)
    /*private fun setUpHomeFeed() {
        val feedAdapter = getAdapter()

        binding.viewpager.adapter = feedAdapter.also { adapter ->
            socketFeedViewModel.videos.onEach {
                hideShimmer()
                val previousSize = adapter.homeResponse.size

                adapter.homeResponse.addAll(it)

                adapter.notifyItemRangeInserted(previousSize, it.size)

            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }

        socketFeedViewModel.events.onEach {
            when (it) {
                SuggestionClicked -> {
                    feedAdapter.homeResponse = mutableSetOf()
                    feedAdapter.notifyDataSetChanged()
                    hideShimmer()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                homeFeedViewModel.homePos.value = position

                with(socketFeedViewModel) {
                    videos.value.getOrNull(position)?.id?.let { suggest(it) }
                    feedAdapter.suggestionVisible(position)

                    if (position == videos.value.size - 3) fetchMoreVideos()
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            socketFeedViewModel.resetTimeline()
            showShimmer()
            binding.swipeRefreshLayout.isRefreshing = false
            feedAdapter.homeResponse = mutableSetOf()
            feedAdapter.notifyDataSetChanged()
        }

        binding.viewpager.setCurrentItem(homeFeedViewModel.homePos.value ?: 0, false)
    }

    private fun getAdapter(): FeedAdapter {
        return FeedAdapter(
            fragmentVideoSuggestion,
            fragmentStories,
            binding.viewpager.context,
            playerPlayBackPosition,
            mutableSetOf(),
            activity,
            binding.storySuggestionViewButton,
            homeFeedViewModel,
            socketFeedViewModel,
            videoRepository,
            this,
            profileRepository
        )
    }*/

    private fun setUpNavViewColor(color: String) {
        if (!isInDarkTheme()) {
            (activity as HomeActivity).binding.navBarView.background =
                ColorDrawable(Color.parseColor(color))
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.onTouchEvent(event)
        /*binding.viewpager.onInterceptTouchEvent(event)*/
        return false
    }

    private fun setUpNavIcon(icon: Drawable?) {
        if (!isInDarkTheme()) {
            if (icon != null) {
                (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.home_flow).icon =
                    icon
            } else {
                (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.home_flow).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_inactivehome)
            }
        }
    }

    /**Function to Stop the Shimmer effect.*/
    /*private fun hideShimmer() {
        binding.homeFeedShimmer.visibility = View.GONE
    }

    private fun showShimmer() {
        binding.homeFeedShimmer.visibility = View.VISIBLE
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        /*if (!isInDarkTheme()) {
            (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.camera_flow)
                .setIcon(R.drawable.ic_camera)
            (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.explore_flow)
                .setIcon(R.drawable.navigation_search_selector)
            if ((activity as HomeActivity).profileDrawable == null) {
                (activity as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.profile_flow)
                    .setIcon(R.drawable.navigation_profile_selector)
            }

        }*/

        /*setUpNavIcon(null)*/
        /*setUpNavViewColor("#4D000000")*/
    }


    override fun onStart() {
        super.onStart()
//        blurLayout.startBlur()

    }

    override fun onStop() {
        super.onStop()
//        blurLayout.pauseBlur()
    }
}
