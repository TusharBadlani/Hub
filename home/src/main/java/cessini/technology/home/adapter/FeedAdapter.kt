package cessini.technology.home.adapter

import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.`class`.ExoProvider
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.commonui.utils.dp
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.home.R
import cessini.technology.home.databinding.HomeFeedRecyclerItemBinding
import cessini.technology.home.fragment.HomeFragment
import cessini.technology.home.fragment.StoriesFragment
import cessini.technology.home.fragment.VideoSuggestionFragment
import cessini.technology.home.utils.Constant
import cessini.technology.home.viewmodel.HomeFeedViewModel
import cessini.technology.home.viewmodel.SocketFeedViewModel
import cessini.technology.model.Video
import cessini.technology.model.VideoProfile
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.myworld.ProfileRepository
import cessini.technology.newrepository.video.VideoRepository
import de.hdodenhof.circleimageview.CircleImageView
import kohii.v1.core.Common
import kohii.v1.core.Playback
import kohii.v1.core.controller
import kotlinx.coroutines.launch
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.M)
class FeedAdapter(
    var fragmentVideoSuggestion: VideoSuggestionFragment,
    var fragmentStories: StoriesFragment,
    var context: Context,
    playbackState: Long,
    var homeResponse: MutableSet<Video>,
    var activity: FragmentActivity?,
    private var storySuggestionViewButton: CircleImageView,
    var homeFeedViewModel: HomeFeedViewModel,
    val socketFeedViewModel: SocketFeedViewModel,
    private val videoRepository: VideoRepository,
    val homeFragment: HomeFragment,
    val profileRepository: ProfileRepository
) : RecyclerView.Adapter<FeedAdapter.MyViewHolder>(),
    View.OnTouchListener,
    GestureDetector.OnGestureListener {

    companion object {
        private const val TAG = "FeedAdapter"
        const val MIN_DISTANCE_HORIZONTAL_SWIPE = 20.0
        const val MIN_DISTANCE_VERTICAL_SWIPE = 0.01
    }

    /**Variables for the touch detection */
    private var x1: Float = 0.0f
    private var x2: Float = 0.0f
    private var y1: Float = 0.0f
    private var y2: Float = 0.0f

    /**Variable to Count if there is any Repeat in video track.*/
    private var videoTrackRepeatModeCount: Int = 0

    lateinit var binding: HomeFeedRecyclerItemBinding

    private var position = 0
    private lateinit var holder: MyViewHolder

    private var video: Video? = null

    private lateinit var gestureDetector: GestureDetector

    private var roomCount = MutableLiveData<Int>()

    inner class MyViewHolder(val binding: HomeFeedRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(homeFeedResponseItem: Video) {
            getCurrentUserRoomCount(homeFeedResponseItem.profile.id)
            binding.exoPlayerHomeFragment.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    if (view != null) {
                        outline?.setRoundRect(0,0,view.width, view.height + 15.dp, 15.dp.toFloat())
                    }
                }
            }
            binding.exoPlayerHomeFragment.clipToOutline = true
            ExoProvider.get(activity!!).setUp(homeFeedResponseItem.url) {
                tag = homeFeedResponseItem.url
                preload = true
                repeatMode = Common.REPEAT_MODE_ALL
                controller = controller { playback, _ ->
                    binding.constraintLayout2.setOnClickListener {
                        with(playback.playable) {
                            this ?: return@setOnClickListener

                            binding.exoPlayerHomeFragment.player?.playWhenReady = !isPlaying()

                            if (!isPlaying()) binding.imgPlay.visibility = View.VISIBLE
                        }
                    }
                    playback.addStateListener(object : Playback.StateListener {
                        override fun onBuffering(playback: Playback, playWhenReady: Boolean) {
                            binding.videoProgressBar.visibility = View.VISIBLE
                        }

                        override fun onPlaying(playback: Playback) {
                            binding.imgPlay.visibility = View.GONE
                            binding.videoProgressBar.visibility = View.GONE
                        }

                        override fun onRendered(playback: Playback) {
                            homeFeedViewModel.currVideoId.value = homeFeedResponseItem.id
                            (activity as HomeActivity).commentsViewModel.setCurrVideoId(homeFeedResponseItem.id)
                            binding.imgPlay.visibility = View.GONE
                            binding.videoProgressBar.visibility = View.GONE
                        }
                    })
                }
            }.bind(binding.exoPlayerHomeFragment)

            activity?.lifecycleScope?.launch {
                binding.videoViewCount.text = homeFeedViewModel.countView(homeFeedResponseItem.id)
            }

            /** Binding the thumbnail of the video.*/
            binding.profileImage = homeFeedResponseItem.profile.picture

            /**Binding the video Description.*/
            binding.userFeedDiscription.text = homeFeedResponseItem.title

            /**Binding the video title.*/
            binding.userFeedName.text = homeFeedResponseItem.profile.name

            binding.roomBadge.text = homeFeedResponseItem.roomCount.toString()

            Log.d(TAG, "$homeFeedResponseItem and ${holder.binding.videoViewCount.text}")
        }

        private fun setLikeButtonTint(liked: Boolean) {
            binding.userLike.setImageResource(
                if (liked) R.drawable.ic_likeactive else R.drawable.ic_like_inactive
            )
        }

        /** This function works according to the click made on views . */
        fun onClick(
            video: Video,
            fragStories: StoriesFragment,
            storySuggestionViewButton: CircleImageView,
            fragmentVideoSuggestion: VideoSuggestionFragment
        ) {
            storySuggestionViewButton.setOnClickListener {
                if (fragmentVideoSuggestion.view?.visibility == View.VISIBLE) {
                    fragmentVideoSuggestion.view?.visibility = View.GONE
                }
                if (fragStories.view?.visibility == View.GONE) {
                    fragStories.view?.visibility = View.VISIBLE
                } else {
                    fragStories.view?.visibility = View.GONE
                    (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.CameraFlow)
                }
            }

            activity?.lifecycleScope?.launch {
                runCatching { setLikeButtonTint(videoRepository.liked(video.id)) }
            }

            holder.binding.userLike.setOnClickListener {
                if ((activity as HomeActivity).baseViewModel.authFlag.value == false) {
                    /*TODO: Use some other method to navigate to authFlow
                       homeFeedViewModel.goToAuth()*/
                } else {
                    activity?.lifecycleScope?.launch {
                        val videoLikeStatus = homeFeedViewModel.postLikeOnVideo(video.id)
                        setLikeButtonTint(videoLikeStatus)
                    }
                }
            }

            /**Comment on a video.*/
            holder.binding.userComment.setOnClickListener {
                (activity as HomeActivity).commentsViewModel.getCommentsList(video.id)
//                homeFragment.findNavController()
//                    .navigate(HomeFragmentDirections.actionHomeFragmentToCommentFragment())
            }

            /**Visit the Profile of the user.*/
            holder.binding.userProfileTham.setOnClickListener {
                openUserProfile(video.profile)
            }

            /**Visit the Profile of the user.*/
            holder.binding.userFeedName.setOnClickListener {
                openUserProfile(video.profile)
            }

            holder.binding.roomIcon.setOnClickListener {
                if (video.profile.id != (activity as HomeActivity).baseViewModel.id.value) {
                    ProfileConstants.creatorModel = SearchCreatorApiResponse(
                        id = video.profile.id,
                        name = video.profile.name,
                        channel_name = video.profile.channel,
                        profile_picture = video.profile.picture,
                    )
                }
                (activity as ToFlowNavigable).navigateToFlow(
                    NavigationFlow.RoomsListFlow(video.profile.id)
                )

            }
        }

    }

    /** Create the View For the Current Item .
     * Also , Initialize the Gesture Detector.
     * Returns the Inflated View to the recycler View , For setting up the UI of current Item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        /** Initialize the gesture detector. */
        gestureDetector = GestureDetector(context, this)
        /** Binding the View with the ViewPager. */
        binding =
            HomeFeedRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }


    /** This function binds the current item data to the view before displaying the view.*/
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        video = homeResponse.elementAt(position)
        homeFragment.viewsUpdater(homeResponse.elementAt(position).id, position)

        this.holder = holder

        /** Binding the data of the video with the UI. */
        holder.bind(video!!)

//        profileViewModel.loadProfile(false)

        /** Actions according to the clickListener. */
        holder.onClick(
            video!!,
            fragmentStories,
            storySuggestionViewButton,
            fragmentVideoSuggestion
        )

        /** Checking the position of the video.
         * If the position is [1] then , the nested suggestion recycler view will be visible.
         * Else , the circular suggestion will be visible
         */
    }

    fun openUserProfile(profile: VideoProfile) {
//        val video = homeResponse[position]

        /**If the profile clicked is not the users profile
         * then , we will navigate them to the global public profile.
         */
        if (profile.id != (activity as HomeActivity).baseViewModel.id.value) {
            ProfileConstants.creatorModel = SearchCreatorApiResponse(
                id = profile.id,
                name = profile.name,
                channel_name = profile.channel,
                profile_picture = profile.picture,
            )
            Log.d("CHECK", ProfileConstants.creatorModel.toString())
            (activity as ToFlowNavigable).navigateToFlow(
                NavigationFlow.GlobalProfileFlow
            )
        }
        /** Else , we will navigate them to there own profile section.*/
        else {
            (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.ProfileFlow)
        }
    }


    /** Checking the position of the video.
     * If the position is [1] then , the nested suggestion recycler view will be visible.
     * Else , the circular suggestion will be visible
     */
    fun suggestionVisible(position: Int) {
        //If the position is equal to 1st video then the nested suggestion recycler view will be visible.
        if (position == 0) {
            fragmentStories.view?.visibility = View.VISIBLE
        } else {
            storySuggestionViewButton.visibility = View.VISIBLE
        }
    }

    /** Reset the suggestion in the view each time the view is attached or detached from the window. */
    private fun resetSuggestionVisible() {
        //If the position is equal to 1st video then the nested suggestion recycler view will be visible.
        if (Constant.homeFeedCurrentPositionAttached == 0) {
            storySuggestionViewButton.visibility = View.VISIBLE
            fragmentStories.view?.visibility = View.VISIBLE
        } else {
            storySuggestionViewButton.visibility = View.VISIBLE
            fragmentStories.view?.visibility = View.GONE
        }
    }

    /** Returns the size of the Video to the recycler view . */
    override fun getItemCount(): Int {
        return homeResponse.size
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
//        Log.i("Touch", "onScroll")
        //Reset The Suggestions
        //resetSuggestionVisible(holder.itemView)
        x1 = e1.x
        y1 = e1.y
        x2 = e2.x
        y2 = e2.y
        if (abs(y2 - y1) > MIN_DISTANCE_VERTICAL_SWIPE) {
            //Detect Scroll from Down to Up
            if (y2 > y1) {
//                Log.i("Swipe", "Up to down")
            }
            //Detect Scroll from Up to Down
            else {
//                Log.i("Swipe", "Down to Up Swipe")
            }
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    /** Play the Video in the view when a new view is attached to the window. */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        this.position = holder.layoutPosition
        Constant.homeFeedCurrentPositionAttached = this.position

        Log.d(TAG, "CurrentView : ${this.position}")
        Log.d(TAG, "View Attached : ${holder.itemView}")

        /** If the position is [0] then ,
         * Viewer suggestion button , and other items visibility are set accordingly.*/
        if (Constant.homeFeedCurrentPositionAttached == 0) {
            fragmentStories.view?.visibility = View.VISIBLE
        }
        /**  If the position is ![0] then ,
         *  The visibility of the items are set accordingly.*/
        else {
            storySuggestionViewButton.visibility = View.VISIBLE
            fragmentStories.view?.visibility = View.GONE
        }
        /** Starting the exoplayer with the current video when the item is attached into the view. */
        holder.binding.exoPlayerHomeFragment.player?.playWhenReady = true

        /** Gesture detection of the swipe in window and setting up the video suggestion accordingly to the swipe.*/
        holder.binding.constraintLayout2.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            if (event != null) {
                when (event.action) {
                    //When we start to swipe
                    0 -> {
                        x1 = event.x
                        y1 = event.y
                    }
                    //When we end to swipe
                    1 -> {
                        x2 = event.x
                        y2 = event.y

                        //Getting Value for the Horizontal Swipe
                        val valueX: Float = x2 - x1

                        /** Detect Horizontal Swipe on the view. */
                        if (abs(valueX) > MIN_DISTANCE_HORIZONTAL_SWIPE) {
                            //Detect Left to Right Swipe
                            if (x2 > x1) {
                                Log.d("Swipe", "Left to right Swipe")

                                resetSuggestionVisible()

                                /** Video Suggestion feed visibility. */
                                fragmentVideoSuggestion.view?.visibility = View.GONE

                            } else if (x1 > x2 && abs(valueX) > MIN_DISTANCE_HORIZONTAL_SWIPE) {
                                //Detect Right to Left Swipe
                                Log.d("Swipe", "Right to left Swipe")

                                if (fragmentStories.view?.visibility == View.VISIBLE) {
                                    fragmentStories.view?.visibility = View.GONE
                                }
                                if (storySuggestionViewButton.visibility == View.VISIBLE) {
                                    storySuggestionViewButton.visibility = View.GONE
                                }

                                /** Video Suggestion feed visibility. */
                                fragmentVideoSuggestion.view?.visibility = View.VISIBLE
                            }
                        } else {
                            Log.d("Reach", "Else Block")
                        }

                    }
                }
            }
            false
        }
    }

    private fun getCurrentUserRoomCount(id: String) {
        activity?.lifecycleScope?.launch {
            val result = kotlin.runCatching { profileRepository.getProfileRooms(id) }
            roomCount.value = result.getOrDefault(emptyList())?.size
        }
    }

    /** Pause the Video On the View when scrolled. */
    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        Constant.homeFeedCurrentPositionDetached = holder.layoutPosition
        Log.d(TAG, "CurrentViewDetached : ${Constant.homeFeedCurrentPositionDetached}")

        fragmentVideoSuggestion.view?.visibility = View.GONE

        /** If the position is [0] then ,
         * Viewer suggestion button , and other items visibility are set accordingly.*/
        if (Constant.homeFeedCurrentPositionDetached == 0) {
            storySuggestionViewButton.visibility = View.VISIBLE
            if (fragmentStories.view?.visibility == View.GONE) {
            } else {
                fragmentStories.view?.visibility = View.GONE
            }
        }
        /**  If the position is ![0] then ,
         *  The visibility of the items are set accordingly.*/
        else {
            storySuggestionViewButton.visibility = View.VISIBLE
        }
    }
}
